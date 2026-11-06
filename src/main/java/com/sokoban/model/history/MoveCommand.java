package com.sokoban.model.history;

import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Game;

/** Mover al Sokoban en una direccion. Es el comando que arma el InputController. */
public class MoveCommand implements Command {

    private final Game game;
    private final Direction direction;

    public MoveCommand(Game game, Direction direction) {
        this.game = game;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void execute() {
        game.move(direction);
    }
}
