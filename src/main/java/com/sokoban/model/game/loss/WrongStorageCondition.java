package com.sokoban.model.game.loss;

import com.sokoban.model.game.Level;
import com.sokoban.model.tile.LandingOutcome;

/** Una caja entro en un destino que no es de su tipo. */
public class WrongStorageCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        return level.getLastOutcome() == LandingOutcome.WRONG_STORAGE;
    }

    @Override
    public LossReason getReason() {
        return LossReason.WRONG_STORAGE;
    }
}
