package com.sokoban.model.tile;

import com.sokoban.model.tile.effect.EntryEffect;
import com.sokoban.model.tile.effect.SlideEffect;

/** Terreno resbaladizo: quien lo pisa se desliza. */
public class Ice extends Tile {

    private static final EntryEffect SLIDE = new SlideEffect();

    @Override
    public EntryEffect getEntryEffect() {
        return SLIDE;
    }
}
