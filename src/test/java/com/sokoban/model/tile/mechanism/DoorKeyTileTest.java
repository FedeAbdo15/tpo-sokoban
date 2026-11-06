package com.sokoban.model.tile.mechanism;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoorKeyTileTest {

    private static Door doorAt(Board board, int row, int col) {
        return assertInstanceOf(Door.class, board.getTile(new Position(row, col)));
    }

    @Test
    @DisplayName("1. Una KeyBox sobre la baldosa a abre todas las puertas A")
    void keyBoxOpensAllDoorsOfItsGroup() {
        Level level = TestLevels.level(
                "#########",
                "#@K a   #",
                "##A###A##",
                "#  ;    #",
                "#########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertTrue(doorAt(level.getBoard(), 2, 2).isOpen());
        assertTrue(doorAt(level.getBoard(), 2, 6).isOpen());
    }

    @Test
    @DisplayName("2. La baldosa llave desaparece despues de usarse")
    void keyTileDisappearsAfterUse() {
        Level level = TestLevels.level(
                "#########",
                "#@K a   #",
                "##A###A##",
                "#  ;    #",
                "#########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertFalse(level.getBoard().getTile(new Position(1, 4)) instanceof KeyTile);
    }

    @Test
    @DisplayName("3. Una NormalBox sobre la baldosa no abre nada")
    void normalBoxDoesNotOpenDoors() {
        Level level = TestLevels.level(
                "########",
                "#@$ a .#",
                "##A#####",
                "#      #",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertFalse(doorAt(level.getBoard(), 2, 2).isOpen());
        assertInstanceOf(KeyTile.class, level.getBoard().getTile(new Position(1, 4)));
    }

    @Test
    @DisplayName("3. Una WeakBox sobre la baldosa no abre nada")
    void weakBoxDoesNotOpenDoors() {
        Level level = TestLevels.level(
                "########",
                "#@5 a :#",
                "##A#####",
                "#      #",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertFalse(doorAt(level.getBoard(), 2, 2).isOpen());
    }

    @Test
    @DisplayName("4. Las puertas del grupo B no se abren con la baldosa a")
    void otherGroupStaysClosed() {
        Level level = TestLevels.level(
                "##########",
                "#@K a  b #",
                "##A####B##",
                "#  ;     #",
                "##########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertTrue(doorAt(level.getBoard(), 2, 2).isOpen());
        assertFalse(doorAt(level.getBoard(), 2, 7).isOpen());
    }

    @Test
    @DisplayName("5. Una puerta cerrada se comporta como pared para el jugador")
    void closedDoorBlocksPlayer() {
        Level level = TestLevels.level(
                "#######",
                "# a $.#",
                "#@A   #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(2, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("5. Una puerta cerrada se comporta como pared para las cajas")
    void closedDoorBlocksBoxes() {
        Level level = TestLevels.level(
                "#######",
                "# a  .#",
                "#@$A  #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertNotNull(level.getBoard().getBoxAt(new Position(2, 2)));
        assertEquals(new Position(2, 1), level.getBoard().getPlayerPosition());
    }
}
