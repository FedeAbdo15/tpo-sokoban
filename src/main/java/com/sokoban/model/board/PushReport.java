package com.sokoban.model.board;

import com.sokoban.model.tile.LandingOutcome;
import com.sokoban.model.box.PushResult;

/** Resultado interno de empujar una caja: como termino, donde quedo y que provoco. */
record PushReport(PushResult result, LandingOutcome outcome, Position landing) {

    static PushReport blocked() {
        return new PushReport(PushResult.BLOCKED, LandingOutcome.NONE, null);
    }
}
