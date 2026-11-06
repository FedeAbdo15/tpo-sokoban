package com.sokoban.model.game.loss;

import com.sokoban.model.game.Level;

public interface LossCondition {

    boolean isMet(Level level);

    LossReason getReason();
}
