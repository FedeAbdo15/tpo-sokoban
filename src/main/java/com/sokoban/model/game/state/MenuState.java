package com.sokoban.model.game.state;

/** Pantalla inicial: todavia no empezo la partida. */
public class MenuState implements GameState {

    @Override
    public boolean acceptsInput() {
        return false;
    }
}
