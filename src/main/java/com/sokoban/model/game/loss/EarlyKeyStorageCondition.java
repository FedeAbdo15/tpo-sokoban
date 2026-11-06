package com.sokoban.model.game.loss;

import com.sokoban.model.game.Level;
import com.sokoban.model.tile.LandingOutcome;

/** La caja llave llego a su destino sin haber abierto antes su muro. */
public class EarlyKeyStorageCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        return level.getLastOutcome() == LandingOutcome.KEY_NOT_USED;
    }

    @Override
    public LossReason getReason() {
        return LossReason.EARLY_KEY_STORAGE;
    }
}
