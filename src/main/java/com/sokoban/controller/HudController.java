package com.sokoban.controller;

import com.sokoban.model.game.Game;
import com.sokoban.view.HudView;

/** Conecta los botones del HUD con la fachada del modelo. */
public class HudController {

    private final Game game;
    private final Runnable afterAction;

    public HudController(Game game, Runnable afterAction) {
        this.game = game;
        this.afterAction = afterAction;
    }

    public void install(HudView hud) {
        hud.getUndoButton().addActionListener(event -> {
            game.undo();
            afterAction.run();
        });
        hud.getRestartButton().addActionListener(event -> {
            game.restart();
            afterAction.run();
        });
        // Los botones no se quedan con el foco: las flechas siguen moviendo al Sokoban.
        hud.getUndoButton().setFocusable(false);
        hud.getRestartButton().setFocusable(false);
    }
}
