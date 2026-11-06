package com.sokoban.model.tile;

import com.sokoban.model.board.Position;
import com.sokoban.model.tile.effect.EntryEffect;
import com.sokoban.model.tile.effect.TeleportEffect;

public class Portal extends Tile {
    private final char letter;
    private final TeleportEffect effect = new TeleportEffect();

    public Portal(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    /** La invoca el LevelBuilder al aparear los dos portales de la misma letra. */
    public void linkTo(Position exit) {
        effect.setExit(exit);
    }

    @Override
    public EntryEffect getEntryEffect() {
        return effect;
    }

    /** La visibilidad atraviesa los portales: lo que se ve desde la salida tambien se ve. */
    @Override
    public Position getLinkedPosition() {
        return effect.getExit();
    }
}
