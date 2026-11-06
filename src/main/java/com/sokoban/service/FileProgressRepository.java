package com.sokoban.service;

import com.sokoban.port.Progress;
import com.sokoban.port.ProgressRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Adaptador del puerto {@link ProgressRepository} contra un archivo de texto.
 *
 * <p>El modelo depende de la interfaz y no de este archivo (DIP): cambiar a una base
 * de datos o a preferencias del sistema no toca una sola linea del modelo.
 */
public class FileProgressRepository implements ProgressRepository {

    private static final int FIRST_LEVEL = 1;

    private final Path file;

    public FileProgressRepository(Path file) {
        this.file = file;
    }

    @Override
    public void save(Progress progress) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.writeString(file, Integer.toString(progress.getLastUnlockedLevel()));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el progreso en " + file, e);
        }
    }

    /** Si el archivo no existe o esta corrupto, se arranca del primer nivel. */
    @Override
    public Progress load() {
        if (!Files.isRegularFile(file)) {
            return new Progress(FIRST_LEVEL);
        }
        try {
            return new Progress(Integer.parseInt(Files.readString(file).trim()));
        } catch (IOException | NumberFormatException e) {
            return new Progress(FIRST_LEVEL);
        }
    }
}
