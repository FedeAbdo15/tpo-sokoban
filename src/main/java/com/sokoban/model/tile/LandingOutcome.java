package com.sokoban.model.tile;

/** Consecuencia de que una caja termine su movimiento sobre una baldosa. */
public enum LandingOutcome {
    /** Nada especial: piso, hielo, portal, o un casillero cerrojo que la caja no activa. */
    NONE,
    /** La caja quedo guardada en su destino. */
    STORED,
    /** La caja entro en un destino de otro tipo. */
    WRONG_STORAGE,
    /** La caja llave llego a su destino sin haber abierto todavia su muro. */
    KEY_NOT_USED,
    /** La caja llave activo un casillero cerrojo y se abrieron los muros de ese grupo. */
    DOOR_OPENED
}
