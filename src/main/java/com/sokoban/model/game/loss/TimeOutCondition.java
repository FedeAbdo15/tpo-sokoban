package com.sokoban.model.game.loss;

import com.sokoban.model.game.Level;

/** Se agoto el tiempo limite del nivel. */
public class TimeOutCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        return level.getRemainingSeconds() <= 0;
    }

    @Override
    public LossReason getReason() {
        return LossReason.TIME_OUT;
    }
}
