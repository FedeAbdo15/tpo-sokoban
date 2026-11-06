package com.sokoban.model.board;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Niebla de guerra: solo se dibuja lo que se alcanza desde el Sokoban.
 *
 * <p>Es un recorrido en anchura sobre las baldosas transparentes. Las cajas no tapan;
 * tapan las paredes, el terreno vacio y los muros cerrados. Al abrirse un muro el nivel
 * vuelve a calcularlo y aparece la zona nueva.
 *
 * <p>El recorrido atraviesa los portales: cada portal encadena con la posicion de su
 * pareja a traves de {@code Tile.getLinkedPosition()}, sin que el mapa tenga que saber
 * que esa baldosa es un portal.
 */
public class VisibilityMap {

    private final Set<Position> visible = new HashSet<>();

    public boolean isVisible(Position position) {
        return visible.contains(position);
    }

    public void recalculate(Board board) {
        visible.clear();
        Position start = board.getPlayerPosition();
        if (start == null) {
            return;
        }
        Deque<Position> pending = new ArrayDeque<>();
        pending.add(start);
        visible.add(start);

        while (!pending.isEmpty()) {
            Position current = pending.removeFirst();
            for (Direction direction : Direction.values()) {
                visit(board, current.move(direction), pending);
            }
            markCorners(board, current);
            visit(board, board.getTile(current).getLinkedPosition(), pending);
        }
    }

    /**
     * Las paredes en diagonal tambien se dibujan: si no, las esquinas de la sala
     * quedarian como agujeros negros aunque el resto del muro se vea.
     */
    private void markCorners(Board board, Position center) {
        for (int rowOffset = -1; rowOffset <= 1; rowOffset += 2) {
            for (int colOffset = -1; colOffset <= 1; colOffset += 2) {
                Position corner = new Position(center.row() + rowOffset, center.col() + colOffset);
                if (board.isInside(corner) && !board.getTile(corner).isTransparent()) {
                    visible.add(corner);
                }
            }
        }
    }

    private void visit(Board board, Position position, Deque<Position> pending) {
        if (position == null || !board.isInside(position) || visible.contains(position)) {
            return;
        }
        // La pared que rodea la zona alcanzable se ve, pero no se sigue por ella.
        visible.add(position);
        if (board.getTile(position).isTransparent()) {
            pending.add(position);
        }
    }
}
