package com.sokoban.model.game.state;

/** Se completaron todos los niveles. */
public class GameCompletedState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }
}
