package com.sokoban.factory;

import com.sokoban.exception.InvalidLevelException;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.BoxType;
import com.sokoban.model.box.KeyBox;
import com.sokoban.model.box.NormalBox;
import com.sokoban.model.box.Player;
import com.sokoban.model.game.Level;
import com.sokoban.model.tile.Floor;
import com.sokoban.model.tile.Ice;
import com.sokoban.model.tile.StoragePoint;
import com.sokoban.model.tile.Empty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Builder director: reads the level text and tells the builder what to create. */
public class LevelLoader {
    private static final String SEPARATOR = "---";

    private final Map<Character, CellCreator> creators = new HashMap<>();

    public LevelLoader() {
        register("#", new WallCreator());
        register("%", (p, symbol) -> new CellContent(new Empty(), null));
        register(" ", (p, symbol) -> new CellContent(new Floor(), null));
        register("@", (p, symbol) -> new CellContent(new Floor(), new Player(Direction.DOWN)));
        register("$", (p, symbol) -> new CellContent(new Floor(), new NormalBox()));
        register("K", (p, symbol) -> new CellContent(new Floor(), new KeyBox()));
        register("123456789", new WeakBoxCreator());
        register(".", (p, symbol) -> new CellContent(new StoragePoint(BoxType.NORMAL), null));
        register(":", (p, symbol) -> new CellContent(new StoragePoint(BoxType.WEAK), null));
        register(";", (p, symbol) -> new CellContent(new StoragePoint(BoxType.KEY), null));
        register("~", (p, symbol) -> new CellContent(new Ice(), null));
        register("abcde", new KeyTileCreator());
        register("ABCDE", new DoorCreator());
        register("PQRS", new PortalCreator());
    }

    private void register(String symbols, CellCreator creator) {
        for (char symbol : symbols.toCharArray()) {
            creators.put(symbol, creator);
        }
    }

    /** Loads a level from the classpath, e.g. "/levels/nivel01.txt". */
    public Level load(String resourcePath) throws InvalidLevelException {
        try (InputStream in = LevelLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new InvalidLevelException("Level not found: " + resourcePath, -1);
            }
            return parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new InvalidLevelException("Could not read " + resourcePath + ": " + e.getMessage(), -1);
        }
    }

    /** Parses the full text of a level file: header, "---" separator and map. */
    public Level parse(String text) throws InvalidLevelException {
        List<String> lines = text.lines().toList();
        int separator = lines.indexOf(SEPARATOR);
        if (separator < 0) {
            throw new InvalidLevelException("Missing '" + SEPARATOR + "' between header and map", -1);
        }
        LevelBuilder builder = new LevelBuilder();
        parseHeader(lines.subList(0, separator), builder);
        builder.withMapStartLine(separator + 2);
        parseMap(lines, separator + 1, builder);
        return builder.build();
    }

    private void parseHeader(List<String> lines, LevelBuilder builder) throws InvalidLevelException {
        Map<String, String> values = new HashMap<>();
        for (String line : lines) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                values.put(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
            }
        }
        builder.withHeader(values.get("nombre"), intValue(values, "tiempo"), intValue(values, "objetivo"));
    }

    private int intValue(Map<String, String> values, String key) throws InvalidLevelException {
        try {
            return Integer.parseInt(values.get(key));
        } catch (NumberFormatException e) {
            throw new InvalidLevelException("Header '" + key + "' must be a number", -1);
        }
    }

    /** Map rows start right after the separator; error lines are 1-based lines of the file. */
    private void parseMap(List<String> lines, int firstRow, LevelBuilder builder) throws InvalidLevelException {
        for (int i = firstRow; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);
                CellCreator creator = creators.get(symbol);
                if (creator == null) {
                    throw new InvalidLevelException("Unknown character '" + symbol + "'", i + 1);
                }
                Position position = new Position(i - firstRow, col);
                builder.addCell(position, creator.create(position, symbol));
            }
        }
    }
}
