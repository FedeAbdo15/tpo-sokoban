package com.sokoban.model.game.event;

import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;

/**
 * Lo que acaba de pasar en el nivel. Lleva la posicion y la direccion para que la vista
 * pueda animar y el servicio de sonido elegir el efecto sin volver a mirar el tablero.
 */
public class GameEvent {
    private final EventType type;
    private final Position position;
    private final Direction direction;

    public GameEvent(EventType type) {
        this(type, null, null);
    }

    public GameEvent(EventType type, Position position, Direction direction) {
        this.type = type;
        this.position = position;
        this.direction = direction;
    }

    public EventType getType() {
        return type;
    }

    /** Donde ocurrio: el jugador, la caja empujada o el casillero activado. Puede ser null. */
    public Position getPosition() {
        return position;
    }

    /** Hacia donde fue el movimiento, o null si el evento no tiene direccion. */
    public Direction getDirection() {
        return direction;
    }
}
