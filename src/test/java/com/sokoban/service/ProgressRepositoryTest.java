package com.sokoban.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.sokoban.port.Progress;
import com.sokoban.port.ProgressRepository;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ProgressRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("1. Guardar y cargar el ultimo nivel desbloqueado")
    void savesAndLoadsLastUnlockedLevel() {
        Path file = tempDir.resolve("progress.txt");

        new FileProgressRepository(file).save(new Progress(3));
        ProgressRepository reopened = new FileProgressRepository(file);

        assertEquals(3, reopened.load().getLastUnlockedLevel());
    }

    @Test
    @DisplayName("2. Si no existe el archivo, el progreso arranca en el nivel 1")
    void startsAtLevelOneWithoutFile() {
        ProgressRepository repository = new FileProgressRepository(tempDir.resolve("missing.txt"));

        assertEquals(1, repository.load().getLastUnlockedLevel());
    }
}
