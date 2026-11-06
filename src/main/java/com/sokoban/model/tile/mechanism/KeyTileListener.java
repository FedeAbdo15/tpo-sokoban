package com.sokoban.model.tile.mechanism;

/** Observer del casillero cerrojo. */
public interface KeyTileListener {

    void onKeyTileActivated(char group);
}
