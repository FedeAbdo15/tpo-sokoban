package com.sokoban.model.board;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisibilityMapTest {

    @Test
    @DisplayName("1. Al cargar, lo que no se alcanza desde el Sokoban queda oculto")
    void unreachableAreaIsHidden() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#######",
                "#     #",
                "#######");

        assertTrue(level.getVisibilityMap().isVisible(new Position(1, 5)));
        assertFalse(level.getVisibilityMap().isVisible(new Position(3, 1)));
    }

    @Test
    @DisplayName("1. Las puertas cuentan como paredes para calcular lo visible")
    void closedDoorsHideTheAreaBehind() {
        Level level = TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  ;   #",
                "########");

        assertTrue(level.getVisibilityMap().isVisible(new Position(1, 6)));
        assertFalse(level.getVisibilityMap().isVisible(new Position(3, 1)));
    }

    @Test
    @DisplayName("2. El calculo atraviesa portales")
    void visibilityGoesThroughPortals() {
        Level level = TestLevels.level(
                "#######",
                "#@P$. #",
                "#######",
                "#  P  #",
                "#######");

        assertTrue(level.getVisibilityMap().isVisible(new Position(3, 1)));
        assertTrue(level.getVisibilityMap().isVisible(new Position(3, 5)));
    }

    @Test
    @DisplayName("3. Al abrir una puerta, se recalcula y se revela la zona nueva")
    void openingDoorRevealsArea() {
        Level level = TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  ;   #",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT); // key box lands on 'a' and opens 'A'

        assertTrue(level.getVisibilityMap().isVisible(new Position(3, 1)));
        assertTrue(level.getVisibilityMap().isVisible(new Position(3, 6)));
    }
}
