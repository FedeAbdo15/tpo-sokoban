package com.sokoban.model.tile.mechanism;

import java.util.ArrayList;
import java.util.List;

/**
 * Todos los muros que comparten una letra. Escucha al casillero cerrojo de su grupo
 * y abre sus muros de una sola vez.
 *
 * <p>{@link Door} y {@link KeyTile} no se conocen entre si: se hablan a traves del grupo.
 */
public class DoorGroup implements KeyTileListener {
    private final char group;
    private final List<Door> doors = new ArrayList<>();

    public DoorGroup(char group) {
        this.group = Character.toUpperCase(group);
    }

    public char getGroup() {
        return group;
    }

    public void add(Door door) {
        doors.add(door);
    }

    public List<Door> getDoors() {
        return List.copyOf(doors);
    }

    public boolean isOpen() {
        return !doors.isEmpty() && doors.stream().allMatch(Door::isOpen);
    }

    public void open() {
        doors.forEach(Door::open);
    }

    /** Lo usa el undo al restaurar un memento anterior a la apertura. */
    public void close() {
        doors.forEach(Door::close);
    }

    @Override
    public void onKeyTileActivated(char activatedGroup) {
        if (activatedGroup == group) {
            open();
        }
    }
}
