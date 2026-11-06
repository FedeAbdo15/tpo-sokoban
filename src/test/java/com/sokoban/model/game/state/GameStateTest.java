package com.sokoban.model.game.state;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private static final int LEVEL_COUNT = 5;

    /** Level won with a single push to the right. */
    private static Level oneMoveLevel() {
        return TestLevels.level("######", "#@$. #", "######");
    }

    /** Level that gets deadlocked when pushing up. */
    private static Level deadlockLevel() {
        return TestLevels.level("#####", "#   #", "#$  #", "#@ .#", "#####");
    }

    /** Level lost when pushing right twice (the weak box breaks). */
    private static Level brokenBoxLevel() {
        return TestLevels.level("#######", "#@1  :#", "#######");
    }

    @Test
    @DisplayName("1. MENU -> PLAYING -> WON -> ... -> GAME_COMPLETED despues del nivel 5")
    void transitionsUntilGameCompleted() {
        Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
        assertInstanceOf(MenuState.class, game.getState());

        game.start();
        assertInstanceOf(PlayingState.class, game.getState());

        for (int number = 1; number <= LEVEL_COUNT; number++) {
            game.move(Direction.RIGHT);
            assertInstanceOf(WonState.class, game.getState(), "level " + number);
            game.nextLevel();
            if (number < LEVEL_COUNT) {
                assertInstanceOf(PlayingState.class, game.getState(), "level " + (number + 1));
            }
        }

        assertInstanceOf(GameCompletedState.class, game.getState());
    }

    @Test
    @DisplayName("1. PLAYING -> BLOCKED cuando hay deadlock")
    void deadlockGoesToBlocked() {
        Game game = new Game(n -> deadlockLevel(), LEVEL_COUNT);
        game.start();

        game.move(Direction.UP);

        assertInstanceOf(BlockedState.class, game.getState());
    }

    @Test
    @DisplayName("1. PLAYING -> LOST cuando se rompe una caja")
    void brokenBoxGoesToLost() {
        Game game = new Game(n -> brokenBoxLevel(), LEVEL_COUNT);
        game.start();

        game.move(Direction.RIGHT);
        game.move(Direction.RIGHT);

        assertInstanceOf(LostState.class, game.getState());
    }

    @Test
    @DisplayName("2. Ganar el nivel N carga el nivel N+1")
    void winningLoadsNextLevel() {
        List<Integer> requested = new ArrayList<>();
        Game game = new Game(n -> {
            requested.add(n);
            return oneMoveLevel();
        }, LEVEL_COUNT);
        game.start();

        game.move(Direction.RIGHT);
        game.nextLevel();

        assertEquals(2, game.getCurrentLevelNumber());
        assertEquals(List.of(1, 2), requested);
    }

    @Test
    @DisplayName("3. Un undo desde BLOCKED vuelve a PLAYING")
    void undoFromBlockedReturnsToPlaying() {
        Game game = new Game(n -> deadlockLevel(), LEVEL_COUNT);
        game.start();
        game.move(Direction.UP);

        game.undo();

        assertInstanceOf(PlayingState.class, game.getState());
    }

    @Test
    @DisplayName("4. Desde WON no se permite undo")
    void undoIsNotAllowedWhenWon() {
        Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
        game.start();
        game.move(Direction.RIGHT);

        game.undo();

        assertInstanceOf(WonState.class, game.getState());
        assertEquals(0, game.getCurrentLevel().getStats().getUndos());
    }

    @Test
    @DisplayName("5. Reiniciar reconstruye el nivel desde el .txt, como si fuera nuevo")
    void restartRebuildsLevel() {
        List<Integer> requested = new ArrayList<>();
        Game game = new Game(n -> {
            requested.add(n);
            return TestLevels.level("#######", "#@ $ .#", "#######");
        }, LEVEL_COUNT);
        game.start();
        Level original = game.getCurrentLevel();
        game.move(Direction.RIGHT);
        game.move(Direction.RIGHT);

        game.restart();

        assertNotSame(original, game.getCurrentLevel());
        assertEquals(List.of(1, 1), requested);
        assertEquals(new Position(1, 1), game.getCurrentLevel().getBoard().getPlayerPosition());
        assertEquals(0, game.getCurrentLevel().getStats().getMoves());
        assertInstanceOf(PlayingState.class, game.getState());
    }
}
