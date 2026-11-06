package com.sokoban.controller;

import com.sokoban.model.board.Direction;
import com.sokoban.model.game.Game;
import com.sokoban.model.history.Command;
import com.sokoban.model.history.MoveCommand;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Controller (GRASP): traduce las teclas direccionales en acciones del modelo.
 *
 * <p>Cada tecla queda asociada a un {@link Command}, no a una llamada suelta: por eso
 * la misma accion sirve despues para un boton o para rebindear la tecla.
 *
 * <p>Usa InputMap/ActionMap en lugar de un KeyListener para no depender de quien tenga
 * el foco dentro de la ventana.
 */
public class InputController {

    private final Game game;
    private final Runnable afterAction;

    public InputController(Game game, Runnable afterAction) {
        this.game = game;
        this.afterAction = afterAction;
    }

    public void install(JComponent component) {
        Map<String, Direction> keys = Map.of(
                "UP", Direction.UP,
                "DOWN", Direction.DOWN,
                "LEFT", Direction.LEFT,
                "RIGHT", Direction.RIGHT);
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();
        keys.forEach((keyName, direction) -> {
            String id = "move_" + keyName;
            inputMap.put(KeyStroke.getKeyStroke(keyName), id);
            actionMap.put(id, run(new MoveCommand(game, direction)));
        });
    }

    /** Asocia una tecla cualquiera a un comando, para los atajos del HUD. */
    public void bind(JComponent component, String keyStroke, Command command) {
        String id = "cmd_" + keyStroke;
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), id);
        component.getActionMap().put(id, run(command));
    }

    private AbstractAction run(Command command) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                command.execute();
                afterAction.run();
            }
        };
    }
}
