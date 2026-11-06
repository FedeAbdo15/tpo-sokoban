package com.sokoban.model.game.state;

import com.sokoban.model.game.Game;

/**
 * Hay un deadlock: el nivel ya no se puede completar, pero el boton deshacer
 * devuelve la partida a un estado jugable.
 */
public class BlockedState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }

    @Override
    public GameState onUndo(Game game) {
        return game.applyUndo() ? new PlayingState() : this;
    }
}
