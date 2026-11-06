package com.sokoban.model.board;

public record Position(int row, int col) {

    public Position move(Direction direction) {
        return new Position(row + direction.getRowOffset(), col + direction.getColOffset());
    }
}
