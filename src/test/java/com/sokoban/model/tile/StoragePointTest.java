package com.sokoban.model.tile;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.box.BoxType;
import com.sokoban.model.box.KeyBox;
import com.sokoban.model.box.NormalBox;
import com.sokoban.model.box.WeakBox;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoragePointTest {

    @Test
    @DisplayName("1. Una caja que entra en un punto de su color queda guardada")
    void boxOnMatchingPointIsStored() {
        assertEquals(StorageResult.STORED, new StoragePoint(BoxType.NORMAL).receive(new NormalBox()));
        assertEquals(StorageResult.STORED, new StoragePoint(BoxType.WEAK).receive(new WeakBox(3)));
    }

    @Test
    @DisplayName("2. Una caja sobre un punto de otro color da WRONG_BOX")
    void boxOnOtherColorIsWrong() {
        assertEquals(StorageResult.WRONG_BOX, new StoragePoint(BoxType.NORMAL).receive(new WeakBox(3)));
        assertEquals(StorageResult.WRONG_BOX, new StoragePoint(BoxType.WEAK).receive(new NormalBox()));
    }

    @Test
    @DisplayName("2. Una WeakBox que pasa sobre un punto de otro color da derrota inmediata")
    void weakBoxOnNormalPointLoses() {
        Level level = TestLevels.level(
                "########",
                "#@3.  :#",
                "#    $ #",
                "########");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("3. Guardar la KeyBox antes de abrir su puerta da derrota (EarlyKeyStorage)")
    void keyBoxStoredBeforeDoorOpenedLoses() {
        assertEquals(StorageResult.KEY_NOT_USED, new StoragePoint(BoxType.KEY).receive(new KeyBox()));

        Level level = TestLevels.level(
                "########",
                "#@K; a #",
                "###A####",
                "#      #",
                "########");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(LossReason.EARLY_KEY_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("4. Guardar la KeyBox despues de haber abierto la puerta es valido")
    void keyBoxStoredAfterDoorOpenedIsValid() {
        KeyBox keyBox = new KeyBox();
        keyBox.markDoorOpened();
        assertEquals(StorageResult.STORED, new StoragePoint(BoxType.KEY).receive(keyBox));

        Level level = TestLevels.level(
                "########",
                "#@Ka;  #",
                "####A###",
                "#      #",
                "########");

        level.move(Direction.RIGHT); // opens door A
        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertNull(level.getLossReason());
    }
}
