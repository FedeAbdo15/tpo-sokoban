package com.sokoban.view;

import com.sokoban.exception.InvalidLevelException;
import com.sokoban.factory.LevelLoader;
import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.state.GameCompletedState;
import com.sokoban.port.ProgressRepository;
import com.sokoban.service.FileProgressRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFlowTest {

    private static final int LEVEL_COUNT = 5;

    private static Level load(int number) {
        try {
            return new LevelLoader().load(String.format("/levels/nivel%02d.txt", number));
        } catch (InvalidLevelException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String[] SOLUTIONS = {
            "IIA DDD BIIIB DDD",
            "IIA DD BIIB DDDD BBIIIIA DDDD",
            "D BIIIIB D BB DDD",
            "DDD AD BBBB III B IB DDD",
            "DD B ADDD B BBBB AIIIIB DDDDDD"
    };

    private static Direction dir(char c) {
        return switch (c) {
            case 'I' -> Direction.LEFT;
            case 'D' -> Direction.RIGHT;
            case 'A' -> Direction.UP;
            case 'B' -> Direction.DOWN;
            default -> throw new IllegalArgumentException();
        };
    }

    @Test
    @org.junit.jupiter.api.DisplayName("El botón 'Volver al menú' regresa al menú principal al completar el juego")
    void testReturnToMenuAfterGameCompleted() {
        Game game = new Game(GameFlowTest::load, LEVEL_COUNT);
        GameWindow window = new GameWindow(game, new SpriteCatalog());
        wireSummary(window, game);

        game.start();
        window.showLevel();

        // Play all 5 levels
        for (int i = 0; i < LEVEL_COUNT; i++) {
            for (char c : SOLUTIONS[i].replace(" ", "").toCharArray()) {
                game.move(dir(c));
            }
            window.refresh();
            if (i < LEVEL_COUNT - 1) {
                // Next level button
                window.getSummaryView().getNextButton().doClick();
            }
        }

        // On level 5 won, screen is SUMMARY, button is "Terminar"
        assertEquals(Screen.SUMMARY, window.getCurrentScreen());
        assertEquals("Terminar", window.getSummaryView().getNextButton().getText());

        // Click "Terminar"
        window.getSummaryView().getNextButton().doClick();

        assertEquals(GameCompletedState.class, game.getState().getClass());
        assertEquals("Volver al menu", window.getSummaryView().getNextButton().getText());
        assertEquals(Screen.SUMMARY, window.getCurrentScreen());

        // Click "Volver al menu"
        window.getSummaryView().getNextButton().doClick();

        assertEquals(Screen.MENU, window.getCurrentScreen());
    }

    private static void wireSummary(GameWindow window, Game game) {
        window.getSummaryView().getNextButton().addActionListener(event -> {
            if (game.getState() instanceof GameCompletedState) {
                window.show(Screen.MENU);
                return;
            }
            if (game.getCurrentLevel().getLossReason() != null) {
                game.restart();
                window.showLevel();
                return;
            }
            game.nextLevel();
            if (game.getState() instanceof GameCompletedState) {
                window.showGameCompleted();
                return;
            }
            window.showLevel();
        });
    }
}
