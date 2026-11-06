package com.sokoban.service;

import com.sokoban.model.game.Game;
import com.sokoban.model.game.event.EventType;
import com.sokoban.model.game.event.GameEvent;
import com.sokoban.model.game.event.GameListener;
import com.sokoban.port.Progress;
import com.sokoban.port.ProgressRepository;

/**
 * Guarda el progreso cuando se gana un nivel.
 *
 * <p>Es un {@link GameListener} para que {@code Game} no tenga que acordarse de
 * persistir: el guardado se engancha a los eventos del modelo igual que el sonido.
 * Depende del puerto {@link ProgressRepository}, no del archivo (DIP).
 */
public class ProgressSaver implements GameListener {

    private final Game game;
    private final ProgressRepository repository;

    public ProgressSaver(Game game, ProgressRepository repository) {
        this.game = game;
        this.repository = repository;
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != EventType.LEVEL_WON) {
            return;
        }
        int unlocked = Math.min(game.getCurrentLevelNumber() + 1, game.getLevelCount());
        if (unlocked > repository.load().getLastUnlockedLevel()) {
            repository.save(new Progress(unlocked));
        }
    }
}
