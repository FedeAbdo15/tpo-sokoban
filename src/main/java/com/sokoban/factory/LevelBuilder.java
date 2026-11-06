package com.sokoban.factory;

import com.sokoban.exception.InvalidLevelException;
import com.sokoban.model.board.Board;
import com.sokoban.model.board.Cell;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Box;
import com.sokoban.model.box.BoxType;
import com.sokoban.model.box.Player;
import com.sokoban.model.game.Level;
import com.sokoban.model.tile.Empty;
import com.sokoban.model.tile.Portal;
import com.sokoban.model.tile.StoragePoint;
import com.sokoban.model.tile.Tile;
import com.sokoban.model.tile.Wall;
import com.sokoban.model.tile.mechanism.Door;
import com.sokoban.model.tile.mechanism.DoorGroup;
import com.sokoban.model.tile.mechanism.KeyTile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Builder + Creator (GRASP): junta las celdas parseadas, las valida y arma el Level.
 *
 * <p>Es el unico que conoce todas las posiciones a la vez, asi que tambien es quien
 * aparea los portales y arma los grupos de muros con su casillero cerrojo.
 */
public class LevelBuilder {
    private String name;
    private int timeLimit;
    private int targetScore;
    private final Map<Position, CellContent> contents = new LinkedHashMap<>();
    private int rows;
    private int cols;
    private int mapStartLine = 1;

    public LevelBuilder withHeader(String name, int timeLimit, int targetScore) {
        this.name = name;
        this.timeLimit = timeLimit;
        this.targetScore = targetScore;
        return this;
    }

    /** Linea del archivo (1-based) donde empieza el mapa, para poder informar errores. */
    public LevelBuilder withMapStartLine(int mapStartLine) {
        this.mapStartLine = mapStartLine;
        return this;
    }

    public LevelBuilder addCell(Position p, CellContent c) {
        contents.put(p, c);
        rows = Math.max(rows, p.row() + 1);
        cols = Math.max(cols, p.col() + 1);
        return this;
    }

    public Level build() throws InvalidLevelException {
        validate();
        Cell[][] cells = new Cell[rows][cols];
        Position playerPosition = null;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position p = new Position(row, col);
                CellContent content = contents.get(p);
                if (content == null) {
                    // las filas cortas se rellenan con terreno vacio
                    cells[row][col] = new Cell(p, new Empty());
                    continue;
                }
                cells[row][col] = new Cell(p, content.tile());
                if (content.occupant() != null) {
                    cells[row][col].place(content.occupant());
                }
                if (content.occupant() instanceof Player) {
                    playerPosition = p;
                }
            }
        }
        linkPortals();
        wireDoorGroups();
        return new Level(name, timeLimit, targetScore, new Board(cells, playerPosition));
    }

    /** Cada letra de portal tiene exactamente dos baldosas: cada una apunta a la otra. */
    private void linkPortals() {
        for (List<Entry<Position, Portal>> pair : portalsByLetter().values()) {
            Entry<Position, Portal> first = pair.get(0);
            Entry<Position, Portal> second = pair.get(1);
            first.getValue().linkTo(second.getKey());
            second.getValue().linkTo(first.getKey());
        }
    }

    /**
     * Observer: cada casillero cerrojo queda suscrito al grupo de muros de su letra.
     * El casillero no conoce los muros, ni los muros al casillero.
     */
    private void wireDoorGroups() {
        Map<Character, DoorGroup> groups = new TreeMap<>();
        for (CellContent content : contents.values()) {
            if (content.tile() instanceof Door door) {
                groups.computeIfAbsent(door.getGroup(), DoorGroup::new).add(door);
            }
        }
        for (CellContent content : contents.values()) {
            if (content.tile() instanceof KeyTile keyTile) {
                DoorGroup group = groups.get(keyTile.getGroup());
                if (group != null) {
                    keyTile.addListener(group);
                }
            }
        }
    }

    private Map<Character, List<Entry<Position, Portal>>> portalsByLetter() {
        Map<Character, List<Entry<Position, Portal>>> byLetter = new TreeMap<>();
        for (Entry<Position, CellContent> entry : contents.entrySet()) {
            if (entry.getValue().tile() instanceof Portal portal) {
                byLetter.computeIfAbsent(portal.getLetter(), letter -> new ArrayList<>())
                        .add(Map.entry(entry.getKey(), portal));
            }
        }
        return byLetter;
    }

    // ------------------------------------------------------------------ validaciones

    private void validate() throws InvalidLevelException {
        validatePlayer();
        validateBoxesAgainstStorages();
        validateDoorGroups();
        validatePortals();
        validateBorder();
    }

    private void validatePlayer() throws InvalidLevelException {
        long players = contents.values().stream().filter(c -> c.occupant() instanceof Player).count();
        if (players != 1) {
            throw new InvalidLevelException(
                    "El nivel debe tener exactamente un Sokoban, encontrados " + players, -1);
        }
    }

    /** Tiene que haber tantas cajas de cada tipo como destinos de ese tipo. */
    private void validateBoxesAgainstStorages() throws InvalidLevelException {
        Map<BoxType, Integer> boxes = new EnumMap<>(BoxType.class);
        Map<BoxType, Integer> storages = new EnumMap<>(BoxType.class);
        for (CellContent content : contents.values()) {
            if (content.occupant() instanceof Box box) {
                boxes.merge(box.getType(), 1, Integer::sum);
            }
            if (content.tile() instanceof StoragePoint point) {
                storages.merge(point.getType(), 1, Integer::sum);
            }
        }
        for (BoxType type : BoxType.values()) {
            int found = boxes.getOrDefault(type, 0);
            int expected = storages.getOrDefault(type, 0);
            if (found != expected) {
                throw new InvalidLevelException("Hay " + found + " cajas de tipo " + type
                        + " y " + expected + " destinos para ellas", -1);
            }
        }
    }

    /** Cada casillero cerrojo necesita su muro, y cada muro su casillero. */
    private void validateDoorGroups() throws InvalidLevelException {
        Set<Character> keyTiles = new TreeSet<>();
        Set<Character> doors = new TreeSet<>();
        for (CellContent content : contents.values()) {
            if (content.tile() instanceof KeyTile keyTile) {
                keyTiles.add(keyTile.getGroup());
            }
            if (content.tile() instanceof Door door) {
                doors.add(door.getGroup());
            }
        }
        for (char group : keyTiles) {
            if (!doors.contains(group)) {
                throw new InvalidLevelException(
                        "El casillero cerrojo del grupo " + group + " no tiene ningun muro", -1);
            }
        }
        for (char group : doors) {
            if (!keyTiles.contains(group)) {
                throw new InvalidLevelException(
                        "El muro del grupo " + group + " no tiene ningun casillero cerrojo", -1);
            }
        }
    }

    /** Cada letra de portal tiene que aparecer exactamente dos veces. */
    private void validatePortals() throws InvalidLevelException {
        for (Entry<Character, List<Entry<Position, Portal>>> entry : portalsByLetter().entrySet()) {
            if (entry.getValue().size() != 2) {
                throw new InvalidLevelException("El portal " + entry.getKey() + " aparece "
                        + entry.getValue().size() + " veces y tiene que aparecer 2", -1);
            }
        }
    }

    /** El borde del mapa tiene que ser pared o terreno vacio: el almacen esta cerrado. */
    private void validateBorder() throws InvalidLevelException {
        for (Entry<Position, CellContent> entry : contents.entrySet()) {
            Position p = entry.getKey();
            boolean onBorder = p.row() == 0 || p.row() == rows - 1
                    || p.col() == 0 || p.col() == cols - 1;
            if (!onBorder) {
                continue;
            }
            Tile tile = entry.getValue().tile();
            if (!(tile instanceof Wall) && !(tile instanceof Empty)) {
                throw new InvalidLevelException(
                        "El borde del mapa esta abierto en la columna " + p.col(),
                        mapStartLine + p.row());
            }
        }
    }
}
