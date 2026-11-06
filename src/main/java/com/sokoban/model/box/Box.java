package com.sokoban.model.box;

import com.sokoban.model.tile.StoragePoint;
import com.sokoban.model.tile.StorageResult;
import com.sokoban.model.tile.Tile;

/**
 * Template Method: {@link #push()} y {@link #storeAt(StoragePoint)} son esqueletos
 * fijos y cada caja solo redefine sus hooks. El tablero mueve la caja; la caja nunca
 * se saca a si misma, informa BROKEN y el tablero la retira.
 */
public abstract class Box implements Occupant {

    public abstract BoxType getType();

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean canEnter(Tile tile) {
        return tile.allowsBox(this);
    }

    /** Hook: solo la caja llave abre muros al llegar a un casillero cerrojo. */
    public boolean opensDoors() {
        return false;
    }

    /** Hook: marca que la caja ya cumplio su funcion de llave. Las demas cajas lo ignoran. */
    public void markDoorOpened() {
        setDoorOpened(true);
    }

    /**
     * Estado interno que el memento necesita guardar y restaurar. Son hooks vacios en la
     * clase base para que el historial nunca tenga que castear a WeakBox ni a KeyBox.
     */
    public boolean hasOpenedDoor() {
        return false;
    }

    public void setDoorOpened(boolean opened) {
    }

    public int getRemainingPushes() {
        return 0;
    }

    public void setRemainingPushes(int remaining) {
    }

    public final PushResult push() {
        return onPushed();
    }

    /** Hook para las subclases. Una caja sin comportamiento extra simplemente se empuja. */
    protected PushResult onPushed() {
        return PushResult.PUSHED;
    }

    public final StorageResult storeAt(StoragePoint point) {
        if (point.getType() != getType()) {
            return StorageResult.WRONG_BOX;
        }
        return onStored(point);
    }

    /** Hook para las cajas con regla propia de guardado. Por defecto, se guarda. */
    protected StorageResult onStored(StoragePoint point) {
        return StorageResult.STORED;
    }
}
