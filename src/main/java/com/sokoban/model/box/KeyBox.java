package com.sokoban.model.box;

import com.sokoban.model.tile.StoragePoint;
import com.sokoban.model.tile.StorageResult;

public class KeyBox extends Box {
    private boolean doorOpened;

    @Override
    public boolean hasOpenedDoor() {
        return doorOpened;
    }

    @Override
    public boolean opensDoors() {
        return true;
    }

    @Override
    public void setDoorOpened(boolean opened) {
        this.doorOpened = opened;
    }

    @Override
    public BoxType getType() {
        return BoxType.KEY;
    }

    /** La caja llave solo puede guardarse despues de haber abierto su muro. */
    @Override
    protected StorageResult onStored(StoragePoint point) {
        return doorOpened ? StorageResult.STORED : StorageResult.KEY_NOT_USED;
    }
}
