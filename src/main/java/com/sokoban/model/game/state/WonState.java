package com.sokoban.model.game.state;

import com.sokoban.model.game.Game;

/** Nivel completado: se muestra el resumen y se espera para pasar al siguiente. */
public class WonState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }

    /** No se permite deshacer una victoria. */
    @Override
    public GameState onUndo(Game game) {
        return this;
    }

    @Override
    public GameState onNextLevel(Game game) {
        if (!game.hasNextLevel()) {
            return new GameCompletedState();
        }
        game.loadLevel(game.getCurrentLevelNumber() + 1);
        return new PlayingState();
    }
}
