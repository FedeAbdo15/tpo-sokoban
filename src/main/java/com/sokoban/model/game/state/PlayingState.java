package com.sokoban.model.game.state;

import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.loss.LossReason;

/** Unico estado que acepta el teclado. */
public class PlayingState implements GameState {

    @Override
    public boolean acceptsInput() {
        return true;
    }

    @Override
    public GameState onMove(Game game, Direction direction) {
        game.applyMove(direction);
        Level level = game.getCurrentLevel();
        if (level.isWon()) {
            return new WonState();
        }
        LossReason reason = level.getLossReason();
        if (reason == null) {
            return this;
        }
        // El deadlock tiene arreglo con el boton deshacer; las demas derrotas no.
        return reason == LossReason.DEADLOCK ? new BlockedState() : new LostState();
    }

    @Override
    public GameState onUndo(Game game) {
        game.applyUndo();
        return this;
    }
}
