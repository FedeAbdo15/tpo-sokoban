package com.sokoban.model.board;

import com.sokoban.model.tile.LandingOutcome;

/**
 * Lo que paso en un movimiento del jugador. El tablero es el Information Expert del
 * movimiento, asi que es el quien informa si hubo empuje y que provoco la caja al caer;
 * el nivel no tiene que deducirlo mirando el tablero antes y despues.
 *
 * @param boxLanding posicion final de la caja empujada, o null si no hubo empuje
 */
public record MoveReport(MoveResult result, boolean pushed, LandingOutcome outcome, Position boxLanding) {

    public static MoveReport blocked() {
        return new MoveReport(MoveResult.BLOCKED, false, LandingOutcome.NONE, null);
    }
}
