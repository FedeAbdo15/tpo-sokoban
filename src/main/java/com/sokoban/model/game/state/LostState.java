package com.sokoban.model.game.state;

import com.sokoban.model.game.Game;

/** Nivel perdido. Igual que el deadlock, se puede volver atras con el boton deshacer. */
public class LostState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }

    @Override
    public GameState onUndo(Game game) {
        return game.applyUndo() ? new PlayingState() : this;
    }
}
