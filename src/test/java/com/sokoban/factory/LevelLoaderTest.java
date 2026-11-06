package com.sokoban.factory;

import com.sokoban.exception.InvalidLevelException;
import com.sokoban.model.board.Board;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.BoxType;
import com.sokoban.model.box.KeyBox;
import com.sokoban.model.box.NormalBox;
import com.sokoban.model.box.WeakBox;
import com.sokoban.model.game.Level;
import com.sokoban.model.tile.Floor;
import com.sokoban.model.tile.Ice;
import com.sokoban.model.tile.Portal;
import com.sokoban.model.tile.StoragePoint;
import com.sokoban.model.tile.Empty;
import com.sokoban.model.tile.Wall;
import com.sokoban.model.tile.mechanism.Door;
import com.sokoban.model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelLoaderTest {

    private final LevelLoader loader = new LevelLoader();

    private InvalidLevelException assertInvalid(String... rows) {
        return assertThrows(InvalidLevelException.class, () -> loader.parse(TestLevels.text(rows)));
    }

    @Test
    @DisplayName("1. Lee el .txt desde el classpath y separa encabezado y mapa con ---")
    void loadsFromClasspathAndSplitsHeaderAndMap() throws InvalidLevelException {
        Level level = loader.load("/levels/test_level.txt");

        assertEquals(3, level.getBoard().getRows());
        assertEquals(6, level.getBoard().getCols());
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Lee bien nombre, tiempo y objetivo del encabezado")
    void readsHeader() throws InvalidLevelException {
        Level level = loader.load("/levels/test_level.txt");

        assertEquals("Nivel de prueba", level.getName());
        assertEquals(90, level.getTimeLimitSeconds());
        assertEquals(500, level.getTargetScore());
    }

    @Test
    @DisplayName("3. Cada caracter crea el elemento correcto")
    void eachCharacterCreatesTheRightElement() {
        Level level = TestLevels.level(
                "#########",
                "#@$1K.:;#",
                "#~aA P P#",
                "#%      #",
                "#########");
        Board board = level.getBoard();

        assertInstanceOf(Wall.class, board.getTile(new Position(0, 0)));
        assertEquals(new Position(1, 1), board.getPlayerPosition());
        assertInstanceOf(Floor.class, board.getTile(new Position(1, 1)));
        assertInstanceOf(NormalBox.class, board.getBoxAt(new Position(1, 2)));
        assertInstanceOf(WeakBox.class, board.getBoxAt(new Position(1, 3)));
        assertInstanceOf(KeyBox.class, board.getBoxAt(new Position(1, 4)));

        StoragePoint normalStorage = assertInstanceOf(StoragePoint.class, board.getTile(new Position(1, 5)));
        StoragePoint weakStorage = assertInstanceOf(StoragePoint.class, board.getTile(new Position(1, 6)));
        StoragePoint keyStorage = assertInstanceOf(StoragePoint.class, board.getTile(new Position(1, 7)));
        assertEquals(BoxType.NORMAL, normalStorage.getType());
        assertEquals(BoxType.WEAK, weakStorage.getType());
        assertEquals(BoxType.KEY, keyStorage.getType());

        assertInstanceOf(Ice.class, board.getTile(new Position(2, 1)));
        KeyTile keyTile = assertInstanceOf(KeyTile.class, board.getTile(new Position(2, 2)));
        Door door = assertInstanceOf(Door.class, board.getTile(new Position(2, 3)));
        assertEquals('A', keyTile.getGroup());
        assertEquals('A', door.getGroup());
        assertFalse(door.isOpen());
        assertInstanceOf(Floor.class, board.getTile(new Position(2, 4)));
        Portal portal = assertInstanceOf(Portal.class, board.getTile(new Position(2, 5)));
        assertEquals('P', portal.getLetter());
        assertInstanceOf(Empty.class, board.getTile(new Position(3, 1)));
    }

    @Test
    @DisplayName("4. El numero de la caja debil (1-9) define cuantos empujes aguanta")
    void weakBoxDigitDefinesPushes() {
        for (int pushes = 1; pushes <= 9; pushes++) {
            Level level = TestLevels.level(
                    "######",
                    "#@" + pushes + " :#",
                    "######");

            WeakBox box = assertInstanceOf(WeakBox.class, level.getBoard().getBoxAt(new Position(1, 2)));
            assertEquals(pushes, box.getRemainingPushes());
        }
    }

    @Test
    @DisplayName("5a. Falla si la cantidad de cajas de un tipo es distinta a la de puntos de su color")
    void failsWhenBoxesAndStorageDoNotMatch() {
        assertInvalid("#####", "#@$ #", "#####");
        assertInvalid("######", "#@$..#", "######");
    }

    @Test
    @DisplayName("5b. Falla si hay una baldosa sin puerta o una puerta sin baldosa")
    void failsWhenKeyTileAndDoorDoNotMatch() {
        assertInvalid("#######", "#@$.a #", "#######");
        assertInvalid("#######", "#@$.A #", "#######");
    }

    @Test
    @DisplayName("5b. Falla si una letra de portal no aparece exactamente 2 veces")
    void failsWhenPortalLetterIsNotPaired() {
        assertInvalid("#######", "#@$.P #", "#######");
        assertInvalid("#########", "#@$.PPP #", "#########");
    }

    @Test
    @DisplayName("5c. Falla si hay 0 Sokoban o mas de 1")
    void failsWhenPlayerCountIsNotOne() {
        assertInvalid("#####", "# $.#", "#####");
        assertInvalid("######", "#@@$.#", "######");
    }

    @Test
    @DisplayName("5d. Falla si el borde del mapa no es pared ni %, indicando la linea")
    void failsWhenBorderIsOpen() {
        InvalidLevelException e = assertInvalid("#####", "#@$. ", "#####");

        assertEquals(TestLevels.HEADER_LINES + 2, e.getLine());
    }

    @Test
    @DisplayName("5e. Falla si aparece un caracter desconocido, indicando la linea")
    void failsOnUnknownCharacter() {
        InvalidLevelException e = assertInvalid("######", "#@$.X#", "######");

        assertEquals(TestLevels.HEADER_LINES + 2, e.getLine());
    }

    @Test
    @DisplayName("6. Los 5 niveles reales (nivel01 a nivel05) cargan sin error")
    void realLevelsLoad() {
        for (int i = 1; i <= 5; i++) {
            String path = String.format("/levels/nivel%02d.txt", i);
            assertDoesNotThrow(() -> loader.load(path), path);
        }
    }
}
