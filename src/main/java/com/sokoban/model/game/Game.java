package com.sokoban.model.game;

import com.sokoban.model.board.Direction;
import com.sokoban.model.game.event.GameListener;
import com.sokoban.model.game.state.GameState;
import com.sokoban.model.game.state.MenuState;
import com.sokoban.model.game.state.PlayingState;

import java.util.function.IntFunction;

/**
 * Fachada del modelo y Context del patron State.
 *
 * <p>Es la unica puerta de entrada del controlador: la vista nunca habla con
 * {@code Level} ni con {@code Board} para cambiar algo.
 *
 * <p>No tiene ningun {@code if} sobre el estado. Cada accion se delega al estado
 * actual, que devuelve el estado siguiente.
 */
public class Game {

    private final IntFunction<Level> levelFactory;
    private final int levelCount;

    private final java.util.List<GameListener> levelListeners = new java.util.ArrayList<>();

    private GameState state = new MenuState();
    private Level currentLevel;
    private int currentLevelNumber;

    /**
     * @param levelFactory construye un nivel nuevo a partir de su numero (1-based)
     * @param levelCount   cantidad de niveles del juego
     */
    public Game(IntFunction<Level> levelFactory, int levelCount) {
        this.levelFactory = levelFactory;
        this.levelCount = levelCount;
    }

    public GameState getState() {
        return state;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void start() {
        loadLevel(1);
        state = new PlayingState();
    }

    public void move(Direction direction) {
        if (!state.acceptsInput()) {
            return;
        }
        state = state.onMove(this, direction);
    }

    public void undo() {
        state = state.onUndo(this);
    }

    /** Reconstruye el nivel actual desde cero, como si recien se cargara. */
    public void restart() {
        loadLevel(currentLevelNumber);
        state = new PlayingState();
    }

    public void nextLevel() {
        state = state.onNextLevel(this);
    }

    public boolean isInputBlocked() {
        return !state.acceptsInput();
    }

    // ------------------------------------------------------- API que usan los estados

    public void applyMove(Direction direction) {
        currentLevel.move(direction);
    }

    public boolean applyUndo() {
        return currentLevel.undo();
    }

    public boolean hasNextLevel() {
        return currentLevelNumber < levelCount;
    }

    public void loadLevel(int number) {
        currentLevelNumber = number;
        currentLevel = levelFactory.apply(number);
        levelListeners.forEach(currentLevel::addListener);
    }

    /**
     * Oyentes que se reenganchan automaticamente a cada nivel que se carga.
     *
     * <p>Reiniciar o pasar de nivel construye un Level nuevo: sin esto, la vista y el
     * sonido se quedarian suscritos al nivel viejo.
     */
    public void addLevelListener(GameListener listener) {
        levelListeners.add(listener);
        if (currentLevel != null) {
            currentLevel.addListener(listener);
        }
    }
}
