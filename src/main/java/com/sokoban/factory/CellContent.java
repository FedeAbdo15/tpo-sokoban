package com.sokoban.factory;

import com.sokoban.model.box.Occupant;
import com.sokoban.model.tile.Tile;

/** What a map character produces: a tile and, optionally, an occupant (player or box). */
public record CellContent(Tile tile, Occupant occupant) {
}
