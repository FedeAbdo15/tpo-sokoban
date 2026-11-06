package com.sokoban.model.history;

import com.sokoban.model.box.Box;

/**
 * Estado interno de una caja en un instante dado.
 *
 * <p>Guarda la referencia a la caja, no una copia: lo que cambia con el tiempo es su
 * estado mutable (la resistencia de la caja fragil y si la caja llave ya abrio su muro).
 * La posicion no va aca: la restaura {@link LevelMemento} junto con la celda.
 */
public record BoxSnapshot(Box box, int remainingPushes, boolean doorOpened) {

    public static BoxSnapshot of(Box box) {
        return new BoxSnapshot(box, box.getRemainingPushes(), box.hasOpenedDoor());
    }

    public void restore() {
        box.setRemainingPushes(remainingPushes);
        box.setDoorOpened(doorOpened);
    }
}
