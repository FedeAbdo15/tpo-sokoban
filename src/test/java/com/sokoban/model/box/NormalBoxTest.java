package com.sokoban.model.box;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalBoxTest {

    @Test
    @DisplayName("1. Empujar una NormalBox a piso libre: OK, la caja avanza 1, el jugador ocupa su lugar y suma 1 empuje")
    void pushToFreeFloorMovesBoxAndPlayer() {
        Level level = TestLevels.level(
                "#######",
                "#@$  .#",
                "#######");
        Box box = level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertSame(box, level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(1, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("2. Empujar una NormalBox varias veces nunca da BROKEN y la caja sigue siendo la misma NormalBox")
    void repeatedPushesNeverBreak() {
        Level level = TestLevels.level(
                "#########",
                "#@$    .#",
                "#########");
        Box box = level.getBoard().getBoxAt(new Position(1, 2));

        for (int i = 1; i <= 4; i++) {
            assertEquals(MoveResult.OK, level.move(Direction.RIGHT), "push " + i);
            assertSame(box, level.getBoard().getBoxAt(new Position(1, 2 + i)), "push " + i);
        }

        assertEquals(BoxType.NORMAL, box.getType());
        assertEquals(4, level.getStats().getPushes());
    }

    @Test
    @DisplayName("3. Caminar sin empujar no afecta a la NormalBox")
    void walkingDoesNotAffectBox() {
        Level level = TestLevels.level(
                "#######",
                "#  $ .#",
                "#@    #",
                "#######");
        Box box = level.getBoard().getBoxAt(new Position(1, 3));

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertSame(box, level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(3, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }
}
