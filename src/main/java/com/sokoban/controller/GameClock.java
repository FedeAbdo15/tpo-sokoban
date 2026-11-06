package com.sokoban.controller;

import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;

import javax.swing.Timer;

/**
 * Pure Fabrication: descuenta un segundo por vez sobre el nivel actual.
 *
 * <p>El modelo solo lleva la cuenta ({@code Level.elapseSeconds}); quien decide cuando
 * pasa un segundo es la UI, con un {@link Timer} de Swing que corre en el EDT.
 */
public class GameClock {

    private static final int ONE_SECOND = 1000;

    private final Timer timer;

    public GameClock(Game game, Runnable afterTick) {
        this.timer = new Timer(ONE_SECOND, event -> {
            Level level = game.getCurrentLevel();
            if (level == null || game.isInputBlocked()) {
                return; // el reloj no corre en el menu, ni con el nivel ganado o perdido
            }
            level.elapseSeconds(1);
            afterTick.run();
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
