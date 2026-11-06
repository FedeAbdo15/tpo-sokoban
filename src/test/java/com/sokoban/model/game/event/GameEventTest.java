package com.sokoban.model.game.event;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameEventTest {

    private static List<EventType> listen(Level level) {
        List<EventType> events = new ArrayList<>();
        level.addListener(event -> events.add(event.getType()));
        return events;
    }

    @Test
    @DisplayName("1. Mover emite PLAYER_MOVED")
    void moveEmitsPlayerMoved() {
        Level level = TestLevels.level("######", "#@ $.#", "######");
        List<EventType> events = listen(level);

        level.move(Direction.RIGHT);

        assertTrue(events.contains(EventType.PLAYER_MOVED));
        assertFalse(events.contains(EventType.BOX_PUSHED));
    }

    @Test
    @DisplayName("1. Empujar emite BOX_PUSHED")
    void pushEmitsBoxPushed() {
        Level level = TestLevels.level("######", "#@$ .#", "######");
        List<EventType> events = listen(level);

        level.move(Direction.RIGHT);

        assertTrue(events.contains(EventType.BOX_PUSHED));
    }

    @Test
    @DisplayName("1. Ganar emite LEVEL_WON")
    void winEmitsLevelWon() {
        Level level = TestLevels.level("#####", "#@$.#", "#####");
        List<EventType> events = listen(level);

        level.move(Direction.RIGHT);

        assertTrue(events.contains(EventType.LEVEL_WON));
    }

    @Test
    @DisplayName("1. Perder emite LEVEL_LOST")
    void lossEmitsLevelLost() {
        Level level = TestLevels.level("#####", "#   #", "#$  #", "#@ .#", "#####");
        List<EventType> events = listen(level);

        level.move(Direction.UP); // box into a corner -> deadlock

        assertTrue(events.contains(EventType.LEVEL_LOST));
    }

    @Test
    @DisplayName("1. Abrir una puerta emite DOOR_OPENED")
    void openingDoorEmitsDoorOpened() {
        Level level = TestLevels.level(
                "########",
                "#@Ka   #",
                "####A###",
                "#  ;   #",
                "########");
        List<EventType> events = listen(level);

        level.move(Direction.RIGHT);

        assertTrue(events.contains(EventType.DOOR_OPENED));
    }
}
