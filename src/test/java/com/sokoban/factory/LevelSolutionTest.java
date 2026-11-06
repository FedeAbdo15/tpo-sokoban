package com.sokoban.factory;

import com.sokoban.exception.InvalidLevelException;
import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.state.GameCompletedState;
import com.sokoban.model.game.state.WonState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integracion de los 5 niveles reales contra la solucion documentada en
 * {@code docs/soluciones-niveles.md}.
 *
 * <p>Es la red de seguridad del diseno de niveles: si alguien toca un .txt y lo vuelve
 * irresoluble, o si cambia una regla del tablero, esto se pone en rojo.
 *
 * <p>Notacion de las soluciones: I izquierda, D derecha, A arriba, B abajo.
 * Los espacios separan etapas y se ignoran.
 */
class LevelSolutionTest {

    private static final int LEVEL_COUNT = 5;

    private record Solution(String moves, int expectedPushes, int expectedOpenedDoors) {
    }

    private static final Solution[] SOLUTIONS = {
            new Solution("IIA DDD BIIIB DDD", 6, 0),
            new Solution("IIA DD BIIB DDDD BBIIIIA DDDD", 10, 0),
            new Solution("D BIIIIB D BB DDD", 4, 0),
            new Solution("DDD AD BBBB III B IB DDD", 11, 1),
            new Solution("DD B ADDD B BBBB AIIIIB DDDDDD", 12, 1)
    };

    private static Level load(int number) {
        try {
            return new LevelLoader().load(String.format("/levels/nivel%02d.txt", number));
        } catch (InvalidLevelException e) {
            throw new IllegalStateException("El nivel " + number + " no carga: " + e.getMessage(), e);
        }
    }

    private static Direction direction(char code) {
        return switch (code) {
            case 'I' -> Direction.LEFT;
            case 'D' -> Direction.RIGHT;
            case 'A' -> Direction.UP;
            case 'B' -> Direction.DOWN;
            default -> throw new IllegalArgumentException("Movimiento desconocido: " + code);
        };
    }

    @Test
    @DisplayName("1. Los 5 niveles se ganan con la solucion documentada y el juego se completa")
    void everyLevelIsSolvable() {
        Game game = new Game(LevelSolutionTest::load, LEVEL_COUNT);
        game.start();

        for (int number = 1; number <= LEVEL_COUNT; number++) {
            Solution solution = SOLUTIONS[number - 1];
            Level level = game.getCurrentLevel();

            for (char code : solution.moves().replace(" ", "").toCharArray()) {
                game.move(direction(code));
            }

            assertNull(level.getLossReason(), "el nivel " + number + " no se debe perder");
            assertTrue(level.isWon(), "el nivel " + number + " tiene que quedar ganado");
            assertInstanceOf(WonState.class, game.getState(), "nivel " + number);
            assertEquals(solution.expectedPushes(), level.getStats().getPushes(),
                    "empujes del nivel " + number);
            assertEquals(solution.expectedOpenedDoors(), level.getOpenedDoors(),
                    "muros abiertos en el nivel " + number);

            game.nextLevel();
        }

        assertInstanceOf(GameCompletedState.class, game.getState(),
                "despues del nivel 5 el juego queda completado");
    }

    @Test
    @DisplayName("2. Ningun nivel se gana de entrada ni arranca perdido")
    void levelsStartUnsolved() {
        for (int number = 1; number <= LEVEL_COUNT; number++) {
            Level level = load(number);
            assertFalse(level.isWon(), "el nivel " + number + " no puede arrancar ganado");
            assertNull(level.getLossReason(), "el nivel " + number + " no puede arrancar perdido");
            assertTrue(level.getTimeLimitSeconds() > 0, "el nivel " + number + " necesita tiempo limite");
            assertTrue(level.getTargetScore() > 0, "el nivel " + number + " necesita objetivo de puntaje");
            assertNotNull(level.getName(), "el nivel " + number + " necesita nombre");
        }
    }
}
