package com.sokoban.model.game.loss;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Box;
import com.sokoban.model.game.Level;

/**
 * La caja recien empujada quedo en una posicion de la que ya no se puede sacar.
 *
 * <p>Se evalua <b>solo sobre la caja empujada</b>, no sobre todas: una caja que arranca
 * el nivel en una esquina es responsabilidad del diseno del nivel, no una derrota.
 *
 * <p>Dos formas de quedar trabada:
 * <ul>
 *   <li><b>Esquina</b>: bloqueada por paredes en un sentido vertical y en uno horizontal.</li>
 *   <li><b>Bloque 2x2</b>: forma un cuadrado de 2x2 lleno de paredes y cajas.</li>
 * </ul>
 *
 * <p>Una caja ya guardada no cuenta: deja de ser ocupante de la celda, asi que
 * {@code getOccupant()} devuelve null y la condicion no se evalua.
 */
public class DeadlockCondition implements LossCondition {

    @Override
    public boolean isMet(Level level) {
        Position position = level.getLastPushedBoxPosition();
        if (position == null) {
            return false;
        }
        Board board = level.getBoard();
        if (!(board.cellAt(position).getOccupant() instanceof Box)) {
            return false; // se guardo o se rompio: no hay caja trabada
        }
        return isCornered(board, position) || formsBlock(board, position);
    }

    /** Bloqueada por pared arriba o abajo, y a la vez por pared a izquierda o derecha. */
    private boolean isCornered(Board board, Position position) {
        boolean vertical = isWall(board, position.move(Direction.UP))
                || isWall(board, position.move(Direction.DOWN));
        boolean horizontal = isWall(board, position.move(Direction.LEFT))
                || isWall(board, position.move(Direction.RIGHT));
        return vertical && horizontal;
    }

    /** Alguno de los cuatro cuadrados de 2x2 que contienen a la caja esta lleno. */
    private boolean formsBlock(Board board, Position position) {
        for (int rowOffset = -1; rowOffset <= 0; rowOffset++) {
            for (int colOffset = -1; colOffset <= 0; colOffset++) {
                if (isFullBlock(board, position.row() + rowOffset, position.col() + colOffset)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFullBlock(Board board, int row, int col) {
        return isBlocked(board, new Position(row, col))
                && isBlocked(board, new Position(row, col + 1))
                && isBlocked(board, new Position(row + 1, col))
                && isBlocked(board, new Position(row + 1, col + 1));
    }

    /** Para armar un bloque cuentan tanto las paredes como las otras cajas. */
    private boolean isBlocked(Board board, Position position) {
        return isWall(board, position) || board.cellAt(position).getOccupant() instanceof Box;
    }

    private boolean isWall(Board board, Position position) {
        return !board.isInside(position) || !board.getTile(position).isTransparent();
    }

    @Override
    public LossReason getReason() {
        return LossReason.DEADLOCK;
    }
}
