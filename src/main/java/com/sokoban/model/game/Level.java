package com.sokoban.model.game;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveReport;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.board.VisibilityMap;
import com.sokoban.model.box.Occupant;
import com.sokoban.model.box.Player;
import com.sokoban.model.game.event.EventType;
import com.sokoban.model.game.event.GameEvent;
import com.sokoban.model.game.event.GameListener;
import com.sokoban.model.game.loss.BrokenBoxCondition;
import com.sokoban.model.game.loss.DeadlockCondition;
import com.sokoban.model.game.loss.EarlyKeyStorageCondition;
import com.sokoban.model.game.loss.LossCondition;
import com.sokoban.model.game.loss.LossReason;
import com.sokoban.model.game.loss.TimeOutCondition;
import com.sokoban.model.game.loss.WrongStorageCondition;
import com.sokoban.model.history.LevelMemento;
import com.sokoban.model.history.UndoHistory;
import com.sokoban.model.tile.LandingOutcome;

import java.util.ArrayList;
import java.util.List;

public class Level {

    /**
     * Strategy: las derrotas se evaluan en orden y gana la primera que se cumple.
     * Agregar una derrota nueva es agregar un elemento a esta lista (OCP).
     */
    private static final List<LossCondition> LOSS_CONDITIONS = List.of(
            new BrokenBoxCondition(),
            new WrongStorageCondition(),
            new EarlyKeyStorageCondition(),
            new DeadlockCondition(),
            new TimeOutCondition());

    private final String name;
    private final int timeLimitSeconds;
    private final int targetScore;
    private final Board board;
    private final LevelStats stats = new LevelStats();
    private final UndoHistory history = new UndoHistory();
    private final VisibilityMap visibilityMap = new VisibilityMap();
    private final List<GameListener> listeners = new ArrayList<>();

    private LossReason lossReason;
    private LandingOutcome lastOutcome = LandingOutcome.NONE;
    private Position lastPushedBoxPosition;
    private boolean brokenBox;
    private boolean endPublished;
    private int elapsedSeconds;
    private int openedDoors;

    public Level(String name, int timeLimitSeconds, int targetScore, Board board) {
        this.name = name;
        this.timeLimitSeconds = timeLimitSeconds;
        this.targetScore = targetScore;
        this.board = board;
        this.visibilityMap.recalculate(board);
    }

    public String getName() {
        return name;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public Board getBoard() {
        return board;
    }

    public LevelStats getStats() {
        return stats;
    }

    public VisibilityMap getVisibilityMap() {
        return visibilityMap;
    }

    /** Muros abiertos durante el nivel, para el bonus de puntaje. */
    public int getOpenedDoors() {
        return openedDoors;
    }

    // ------------------------------------------------------------------ movimiento

    /** Un movimiento bloqueado no cuenta como movimiento ni como empuje. */
    public MoveResult move(Direction direction) {
        LevelMemento before = LevelMemento.of(board);
        faceTowards(direction);

        MoveReport report = board.movePlayer(direction);
        if (report.result() == MoveResult.BLOCKED) {
            return MoveResult.BLOCKED;
        }

        history.record(before);
        stats.addMove();
        if (report.pushed()) {
            stats.addPush();
        }
        lastOutcome = report.outcome();
        lastPushedBoxPosition = report.boxLanding();
        if (report.result() == MoveResult.BROKEN) {
            brokenBox = true;
        }
        if (lastOutcome == LandingOutcome.DOOR_OPENED) {
            openedDoors++;
        }

        visibilityMap.recalculate(board);
        publishMoveEvents(report, direction);
        evaluateLoss();
        publishOutcomeEvents();
        return report.result();
    }

    private void faceTowards(Direction direction) {
        Occupant occupant = board.cellAt(board.getPlayerPosition()).getOccupant();
        if (occupant instanceof Player player) {
            player.face(direction);
        }
    }

    // ------------------------------------------------------------------ deshacer

    /** Devuelve false cuando el undo no se aplico (historial vacio o sin usos disponibles). */
    public boolean undo() {
        LevelMemento memento = history.undo();
        if (memento == null) {
            return false;
        }
        memento.restoreInto(board);
        stats.addUndo();
        resetAfterUndo();
        visibilityMap.recalculate(board);
        publish(EventType.UNDO, board.getPlayerPosition(), null);
        return true;
    }

    /**
     * El undo limpia la derrota: por eso se puede salir de un deadlock. Nunca produce
     * una victoria, asi que tampoco emite LEVEL_WON.
     */
    private void resetAfterUndo() {
        lossReason = null;
        lastOutcome = LandingOutcome.NONE;
        lastPushedBoxPosition = null;
        brokenBox = false;
        endPublished = false;
    }

    public int getRemainingUndos() {
        return history.getRemainingUndos();
    }

    // ------------------------------------------------------------------ tiempo

    public void elapseSeconds(int seconds) {
        elapsedSeconds = Math.min(timeLimitSeconds, elapsedSeconds + seconds);
        evaluateLoss();
        publishOutcomeEvents();
    }

    public int getRemainingSeconds() {
        return Math.max(0, timeLimitSeconds - elapsedSeconds);
    }

    // ------------------------------------------------------------------ victoria y derrota

    /** Se gana cuando no queda ningun destino esperando su caja. */
    public boolean isWon() {
        return board.isComplete();
    }

    /** Devuelve el motivo de la derrota, o null mientras el nivel no este perdido. */
    public LossReason getLossReason() {
        return lossReason;
    }

    /** La victoria se chequea antes que la derrota: una caja en su destino no es deadlock. */
    private void evaluateLoss() {
        if (lossReason != null || isWon()) {
            return;
        }
        for (LossCondition condition : LOSS_CONDITIONS) {
            if (condition.isMet(this)) {
                lossReason = condition.getReason();
                return;
            }
        }
    }

    // Estado que consultan las LossCondition.

    public boolean hasBrokenBox() {
        return brokenBox;
    }

    public LandingOutcome getLastOutcome() {
        return lastOutcome;
    }

    public Position getLastPushedBoxPosition() {
        return lastPushedBoxPosition;
    }

    // ------------------------------------------------------------------ Observer

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    private void publish(EventType type, Position position, Direction direction) {
        GameEvent event = new GameEvent(type, position, direction);
        for (GameListener listener : List.copyOf(listeners)) {
            listener.onEvent(event);
        }
    }

    private void publishMoveEvents(MoveReport report, Direction direction) {
        publish(EventType.PLAYER_MOVED, board.getPlayerPosition(), direction);
        if (report.pushed()) {
            publish(EventType.BOX_PUSHED, report.boxLanding(), direction);
        }
        if (report.outcome() == LandingOutcome.DOOR_OPENED) {
            publish(EventType.DOOR_OPENED, report.boxLanding(), direction);
        }
    }

    /** El fin del nivel se avisa una sola vez, aunque despues siga corriendo el reloj. */
    private void publishOutcomeEvents() {
        if (endPublished) {
            return;
        }
        if (isWon()) {
            endPublished = true;
            publish(EventType.LEVEL_WON, board.getPlayerPosition(), null);
        } else if (lossReason != null) {
            endPublished = true;
            publish(EventType.LEVEL_LOST, board.getPlayerPosition(), null);
        }
    }
}
