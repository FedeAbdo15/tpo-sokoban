package com.sokoban.model.history;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Caretaker del Memento: guarda las fotos del nivel y decide cuanto se puede retroceder.
 *
 * <p>La regla sale textual de la consigna: "Se permite deshacer los ultimos 15
 * movimientos... cada vez que se presione, restaure el estado del nivel 5 movimientos
 * hacia atras, hasta un maximo de tres usos consecutivos."
 *
 * <p>De ahi que la capacidad sea 5 x 3. "Consecutivos" se interpreta literal: se pueden
 * encadenar 3 usos, y volver a mover reinicia el contador, porque la ventana de 15
 * movimientos se desliza. Con menos de 5 movimientos guardados, el undo vuelve a la
 * foto mas vieja que tenga.
 */
public class UndoHistory {

    public static final int STEPS_PER_UNDO = 5;
    public static final int MAX_CONSECUTIVE_UNDOS = 3;
    public static final int CAPACITY = STEPS_PER_UNDO * MAX_CONSECUTIVE_UNDOS;

    private final Deque<LevelMemento> mementos = new ArrayDeque<>();
    private int remainingUndos = MAX_CONSECUTIVE_UNDOS;

    /** Guarda el estado previo a un movimiento y rehabilita el boton. */
    public void record(LevelMemento memento) {
        mementos.addLast(memento);
        while (mementos.size() > CAPACITY) {
            mementos.removeFirst();
        }
        remainingUndos = MAX_CONSECUTIVE_UNDOS;
    }

    public boolean canUndo() {
        return remainingUndos > 0 && !mementos.isEmpty();
    }

    /** Usos consecutivos que todavia quedan. */
    public int getRemainingUndos() {
        return remainingUndos;
    }

    /**
     * Retrocede hasta {@value #STEPS_PER_UNDO} movimientos.
     *
     * @return la foto a restaurar, o null si no se puede deshacer
     */
    public LevelMemento undo() {
        if (!canUndo()) {
            return null;
        }
        LevelMemento target = null;
        for (int step = 0; step < STEPS_PER_UNDO && !mementos.isEmpty(); step++) {
            target = mementos.removeLast();
        }
        remainingUndos--;
        return target;
    }

    public void clear() {
        mementos.clear();
        remainingUndos = MAX_CONSECUTIVE_UNDOS;
    }
}
