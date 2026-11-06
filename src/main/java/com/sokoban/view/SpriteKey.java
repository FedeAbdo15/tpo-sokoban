package com.sokoban.view;

import com.sokoban.model.board.Direction;

/**
 * Clave inmutable de un sprite. Es la clave del Flyweight {@link SpriteCatalog}.
 *
 * @param name   nombre base del archivo, sin extension
 * @param facing direccion, solo para el Sokoban; null para el resto
 */
public record SpriteKey(String name, Direction facing) {

    public static SpriteKey of(String name) {
        return new SpriteKey(name, null);
    }

    public static SpriteKey player(Direction facing) {
        return new SpriteKey("player", facing);
    }

    /** Nombre del PNG: "wall.png", "player_right.png". */
    public String fileName() {
        String suffix = facing == null ? "" : "_" + facing.name().toLowerCase();
        return name + suffix + ".png";
    }
}
