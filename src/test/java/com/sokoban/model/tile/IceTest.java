package com.sokoban.model.tile;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Assumed rule: whoever is standing on ice keeps moving in the same direction
 * while the next cell is free, so the slide ends on the first non-ice cell
 * or just before an obstacle.
 */
class IceTest {

    @Test
    @DisplayName("1. El jugador que entra al hielo se desliza hasta el final del camino")
    void playerSlidesAcrossIce() {
        Level level = TestLevels.level(
                "########",
                "#@~~~  #",
                "#  $.  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 5), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Una caja empujada sobre el hielo tambien se desliza hasta el final")
    void pushedBoxSlidesAcrossIce() {
        Level level = TestLevels.level(
                "#########",
                "#@$~~~ .#",
                "#########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 6)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("3. El deslizamiento frena antes de una pared")
    void slideStopsBeforeWall() {
        Level level = TestLevels.level(
                "########",
                "#@~~~# #",
                "#  $.  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 4), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. El deslizamiento frena antes de otra caja")
    void slideStopsBeforeBox() {
        Level level = TestLevels.level(
                "########",
                "#@~~~$ #",
                "#   .  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 4), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 5)), "sliding does not push the box");
    }

    @Test
    @DisplayName("4. Si el camino de hielo mide 1 bloque, se desliza solo 1")
    void singleIceTileSlidesOnlyOne() {
        Level level = TestLevels.level(
                "#######",
                "#@~   #",
                "# $.  #",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
    }
}
