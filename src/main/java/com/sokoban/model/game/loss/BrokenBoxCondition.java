package com.sokoban.model.game.loss;

import com.sokoban.model.game.Level;

/** Una caja fragil se quedo sin resistencia: el nivel ya no se puede completar. */
public class BrokenBoxCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        return level.hasBrokenBox();
    }

    @Override
    public LossReason getReason() {
        return LossReason.BROKEN_BOX;
    }
}
