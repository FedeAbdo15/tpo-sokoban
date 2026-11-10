package com.sokoban.model.board;

import com.sokoban.model.box.Box;
import com.sokoban.model.box.Occupant;
import com.sokoban.model.box.PushResult;
import com.sokoban.model.tile.Floor;
import com.sokoban.model.tile.LandingOutcome;
import com.sokoban.model.tile.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Information Expert (GRASP): el tablero conoce todas las celdas, asi que valida
 * y ejecuta los movimientos del jugador.
 *
 * <p>No sabe de tipos concretos de baldosa ni de caja: delega en {@code Tile}
 * (que deja entrar, que efecto aplica, que provoca una caja al caer) y en
 * {@code Occupant} (por polimorfismo, para saber si puede entrar).
 */
public class Board {
    private final Cell[][] cells;
    private Position playerPosition;

    public Board(Cell[][] cells, Position playerPosition) {
        this.cells = cells;
        this.playerPosition = playerPosition;
    }

    public int getRows() {
        return cells.length;
    }

    public int getCols() {
        return cells.length == 0 ? 0 : cells[0].length;
    }

    public Cell cellAt(Position p) {
        return cells[p.row()][p.col()];
    }

    public boolean isInside(Position p) {
        return p.row() >= 0 && p.row() < getRows() && p.col() >= 0 && p.col() < getCols();
    }

    public Tile getTile(Position position) {
        return cellAt(position).getTile();
    }

    /** Puede este ocupante quedarse parado en p: existe, la baldosa lo admite y esta libre. */
    public boolean admits(Position p, Occupant occupant) {
        return isInside(p) && occupant.canEnter(getTile(p)) && cellAt(p).isFree();
    }

    /**
     * Devuelve la caja que hay en la posicion, o null. Incluye la caja ya guardada en un
     * destino, que deja de ser ocupante de la celda para que el jugador pueda pisarla.
     */
    public Box getBoxAt(Position position) {
        Cell cell = cellAt(position);
        if (cell.getOccupant() instanceof Box box) {
            return box;
        }
        return cell.getTile().getStoredBox();
    }

    public void placeBox(Position position, Box box) {
        cellAt(position).place(box);
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    /** Reubica al jugador sin mover nada mas. Solo lo usa el undo al restaurar un memento. */
    public void setPlayerPosition(Position position) {
        this.playerPosition = position;
    }

    /** Todas las celdas en una sola lista, para recorrer el tablero sin indices. */
    public List<Cell> getCells() {
        List<Cell> all = new ArrayList<>(getRows() * getCols());
        for (Cell[] row : cells) {
            all.addAll(Arrays.asList(row));
        }
        return all;
    }

    /** Cajas ya guardadas en su destino, para el bonus de puntaje. */
    public int countStoredBoxes() {
        int stored = 0;
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getTile().getStoredBox() != null) {
                    stored++;
                }
            }
        }
        return stored;
    }

    /** Condicion de victoria: no queda ningun destino esperando su caja. */
    public boolean isComplete() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getTile().isPendingStorage()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Mueve al jugador una celda, empujando la caja que tenga adelante si la hay.
     * Un movimiento bloqueado deja el tablero intacto.
     */
    public MoveReport movePlayer(Direction direction) {
        Occupant player = cellAt(playerPosition).getOccupant();
        Position target = playerPosition.move(direction);
        if (!isInside(target) || !player.canEnter(getTile(target))) {
            return MoveReport.blocked();
        }

        boolean pushing = !cellAt(target).isFree();
        PushReport push = pushing ? pushBox(target, direction)
                : new PushReport(PushResult.PUSHED, LandingOutcome.NONE, null);
        if (push.result() == PushResult.BLOCKED) {
            return MoveReport.blocked();
        }

        Position landing = getTile(target).getEntryEffect().resolve(this, player, target, direction);
        if (landing == null) {
            return MoveReport.blocked();
        }

        cellAt(playerPosition).remove();
        cellAt(landing).place(player);
        playerPosition = landing;

        MoveResult result = push.result() == PushResult.BROKEN ? MoveResult.BROKEN : MoveResult.OK;
        return new MoveReport(result, pushing, push.outcome(), push.landing());
    }

    /**
     * Empuja la caja solo si la celda siguiente la admite y esta libre; nunca dos cajas
     * a la vez. La caja se rompe sin moverse y el tablero es quien la retira.
     */
    private PushReport pushBox(Position from, Direction direction) {
        Cell fromCell = cellAt(from);
        Occupant occupant = fromCell.getOccupant();
        if (!occupant.isPushable()) {
            return PushReport.blocked();
        }
        Box box = (Box) occupant;
        Position to = from.move(direction);
        if (!admits(to, box)) {
            return PushReport.blocked();
        }

        // Se saca primero para que el deslizamiento no se choque con la propia caja.
        fromCell.remove();
        Position landing = getTile(to).getEntryEffect().resolve(this, box, to, direction);
        if (landing == null) {
            fromCell.place(box);
            return PushReport.blocked();
        }

        // El contador de la caja fragil recien baja cuando el empuje ya es valido.
        PushResult result = box.push();
        if (result == PushResult.BROKEN) {
            return new PushReport(result, LandingOutcome.NONE, landing);
        }

        Cell target = cellAt(landing);
        target.place(box);
        LandingOutcome outcome = target.getTile().receiveBox(box);
        applyLanding(target, outcome);
        return new PushReport(result, outcome, landing);
    }

    /** Lo que la baldosa no puede hacer sola: soltar la caja de la celda o reemplazarse. */
    private void applyLanding(Cell cell, LandingOutcome outcome) {
        switch (outcome) {
            case STORED -> cell.remove();              // el destino se queda la caja
            case DOOR_OPENED -> cell.setTile(new Floor()); // el casillero cerrojo se consume
            default -> {
            }
        }
    }
}
