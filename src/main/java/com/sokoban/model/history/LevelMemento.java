package com.sokoban.model.history;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Cell;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Box;
import com.sokoban.model.box.Occupant;
import com.sokoban.model.box.Player;
import com.sokoban.model.tile.Tile;
import com.sokoban.model.tile.mechanism.Door;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Memento: foto completa del tablero en un instante.
 *
 * <p><b>Por que Memento y no deshacer el movimiento al reves.</b> Con hielo, portales,
 * muros que se abren y casilleros cerrojo que se consumen, invertir un movimiento es
 * irresoluble: un solo empuje puede mover una caja cuatro celdas, teletransportarla,
 * abrir tres muros y consumir una baldosa. Restaurar una foto es correcto por
 * construccion y no depende de cuantos efectos se encadenaron.
 *
 * <p>Guarda referencias a los objetos del tablero y, aparte, su estado mutable. De esa
 * forma al restaurar vuelven las mismas cajas y las mismas baldosas, no copias.
 *
 * <p>Los contadores de {@code LevelStats} <b>no</b> se guardan a proposito: los
 * movimientos, empujes y undos realmente ocurrieron y el puntaje los penaliza.
 */
public final class LevelMemento {

    /** Que baldosa y que ocupante tenia cada celda. */
    private record CellSnapshot(Cell cell, Tile tile, Occupant occupant) {
    }

    /** Si cada muro estaba abierto o cerrado. */
    private record DoorSnapshot(Door door, boolean open) {
    }

    /** Que caja tenia guardada cada destino. */
    private record StorageSnapshot(Tile point, Box storedBox) {
    }

    private final Position playerPosition;
    private final Direction playerFacing;
    private final List<CellSnapshot> cells;
    private final List<DoorSnapshot> doors;
    private final List<StorageSnapshot> storages;
    private final List<BoxSnapshot> boxes;

    private LevelMemento(Position playerPosition, Direction playerFacing, List<CellSnapshot> cells,
                         List<DoorSnapshot> doors, List<StorageSnapshot> storages, List<BoxSnapshot> boxes) {
        this.playerPosition = playerPosition;
        this.playerFacing = playerFacing;
        this.cells = cells;
        this.doors = doors;
        this.storages = storages;
        this.boxes = boxes;
    }

    /** Toma la foto. El Originator es el Level, que delega aca para no conocer cada baldosa. */
    public static LevelMemento of(Board board) {
        List<CellSnapshot> cells = new ArrayList<>();
        List<DoorSnapshot> doors = new ArrayList<>();
        List<StorageSnapshot> storages = new ArrayList<>();
        Set<Box> seen = new LinkedHashSet<>();

        for (Cell cell : board.getCells()) {
            Tile tile = cell.getTile();
            cells.add(new CellSnapshot(cell, tile, cell.getOccupant()));
            if (tile instanceof Door door) {
                doors.add(new DoorSnapshot(door, door.isOpen()));
            }
            if (tile.getStoredBox() != null || tile.isPendingStorage()) {
                storages.add(new StorageSnapshot(tile, tile.getStoredBox()));
            }
            if (cell.getOccupant() instanceof Box box) {
                seen.add(box);
            }
            if (tile.getStoredBox() != null) {
                seen.add(tile.getStoredBox());
            }
        }

        List<BoxSnapshot> boxes = new ArrayList<>(seen.size());
        for (Box box : seen) {
            boxes.add(BoxSnapshot.of(box));
        }

        Position playerPosition = board.getPlayerPosition();
        Occupant player = board.cellAt(playerPosition).getOccupant();
        Direction facing = player instanceof Player p ? p.getFacing() : Direction.DOWN;
        return new LevelMemento(playerPosition, facing, cells, doors, storages, boxes);
    }

    /** Devuelve el tablero exactamente al estado de la foto. */
    public void restoreInto(Board board) {
        for (CellSnapshot snapshot : cells) {
            snapshot.cell().setTile(snapshot.tile());
            snapshot.cell().place(snapshot.occupant());
        }
        for (DoorSnapshot snapshot : doors) {
            if (snapshot.open()) {
                snapshot.door().open();
            } else {
                snapshot.door().close();
            }
        }
        for (StorageSnapshot snapshot : storages) {
            snapshot.point().setStoredBox(snapshot.storedBox());
        }
        for (BoxSnapshot snapshot : boxes) {
            snapshot.restore();
        }
        board.setPlayerPosition(playerPosition);
        if (board.cellAt(playerPosition).getOccupant() instanceof Player player) {
            player.face(playerFacing);
        }
    }
}
