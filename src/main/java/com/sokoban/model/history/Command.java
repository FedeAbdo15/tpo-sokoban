package com.sokoban.model.history;

/**
 * Command: encapsula una accion del jugador como objeto.
 *
 * <p>Permite que la vista Swing asocie una tecla o un boton a una accion sin conocer
 * el modelo: el controlador arma el comando y lo ejecuta.
 *
 * <p>El deshacer <b>no</b> se resuelve invirtiendo comandos sino restaurando un
 * {@link LevelMemento}: ver la explicacion en esa clase.
 */
public interface Command {

    void execute();
}
