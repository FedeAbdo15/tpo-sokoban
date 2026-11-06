package com.sokoban.model.board;

import com.sokoban.model.box.Occupant;
import com.sokoban.model.tile.Tile;

public class Cell {
    private final Position position;
    private Tile tile;
    private Occupant occupant;

    public Cell(Position position, Tile tile) {
        this.position = position;
        this.tile = tile;
    }

    public Position getPosition() {
        return position;
    }

    public Tile getTile() {
        return tile;
    }

    /** La baldosa cambia cuando un casillero cerrojo se consume y queda piso. */
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /** Devuelve el ocupante de la celda, o null si esta libre. */
    public Occupant getOccupant() {
        return occupant;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public void place(Occupant o) {
        this.occupant = o;
    }

    /** Saca al ocupante de la celda y lo devuelve. */
    public Occupant remove() {
        Occupant removed = occupant;
        occupant = null;
        return removed;
    }
}
