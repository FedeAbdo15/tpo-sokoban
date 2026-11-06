package com.sokoban.factory;

import com.sokoban.model.board.Position;
import com.sokoban.model.box.WeakBox;
import com.sokoban.model.tile.Floor;

/** '1'..'9': the digit is the number of pushes the box withstands. */
public class WeakBoxCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Floor(), new WeakBox(symbol - '0'));
    }
}
