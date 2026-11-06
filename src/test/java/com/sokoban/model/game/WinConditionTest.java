package com.sokoban.model.game;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.game.event.EventType;
import com.sokoban.model.game.state.WonState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WinConditionTest {

    @Test
    @DisplayName("1. Con todas las cajas guardadas se gana, sin importar donde este el jugador")
    void winsWhenAllBoxesAreStored() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#  $. #",
                "#######");

        level.move(Direction.RIGHT); // first box stored, player at (1,2)
        assertFalse(level.isWon());

        level.move(Direction.DOWN);  // player at (2,2)
        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertTrue(level.isWon());
        assertEquals(new Position(2, 3), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. La victoria se chequea despues de un empuje")
    void winIsCheckedAfterPush() {
        Level level = TestLevels.level(
                "#####",
                "#@$.#",
                "#####");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertTrue(level.isWon());
    }

    @Test
    @DisplayName("2. La victoria se chequea despues de un deslizamiento")
    void winIsCheckedAfterSlide() {
        Level level = TestLevels.level(
                "######",
                "#@$~.#",
                "######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertTrue(level.isWon());
    }

    @Test
    @DisplayName("3. Un undo nunca produce una victoria")
    void undoNeverWins() {
        Level level = TestLevels.level(
                "#######",
                "#@$ . #",
                "#######");
        List<EventType> events = new ArrayList<>();
        level.addListener(event -> events.add(event.getType()));

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT); // box stored -> won
        assertTrue(level.isWon());
        events.clear();

        level.undo();

        assertFalse(events.contains(EventType.LEVEL_WON));
    }

    @Test
    @DisplayName("4. Al ganar, se bloquea la entrada del teclado")
    void inputIsBlockedAfterWinning() {
        Game game = new Game(n -> TestLevels.level(
                "######",
                "#@$. #",
                "######"), 5);
        game.start();

        game.move(Direction.RIGHT);

        assertInstanceOf(WonState.class, game.getState());
        assertTrue(game.isInputBlocked());
        Position before = game.getCurrentLevel().getBoard().getPlayerPosition();
        game.move(Direction.LEFT);
        assertEquals(before, game.getCurrentLevel().getBoard().getPlayerPosition());
    }
}
