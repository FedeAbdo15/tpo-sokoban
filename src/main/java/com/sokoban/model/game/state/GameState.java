package com.sokoban.model.game.state;

import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Game;

/**
 * State: cada estado decide que pasa ante cada accion y cual es el estado siguiente.
 *
 * <p>Gracias a esto {@code Game} no tiene un solo {@code if} sobre el estado: delega y
 * reemplaza su estado por el que le devuelven. El bloqueo del teclado tampoco es un
 * booleano suelto, es {@link #acceptsInput()}.
 *
 * <p>Los metodos tienen implementacion por defecto "no hago nada y me quedo como estoy",
 * asi cada estado concreto solo escribe las transiciones que le corresponden.
 */
public interface GameState {

    boolean acceptsInput();

    default GameState onMove(Game game, Direction direction) {
        return this;
    }

    default GameState onUndo(Game game) {
        return this;
    }

    default GameState onNextLevel(Game game) {
        return this;
    }
}
