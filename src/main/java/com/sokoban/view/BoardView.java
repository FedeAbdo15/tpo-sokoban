package com.sokoban.view;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Cell;
import com.sokoban.model.board.Position;
import com.sokoban.model.board.VisibilityMap;
import com.sokoban.model.box.Box;
import com.sokoban.model.box.BoxType;
import com.sokoban.model.box.Player;
import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.event.GameEvent;
import com.sokoban.model.game.event.GameListener;
import com.sokoban.model.tile.Empty;
import com.sokoban.model.tile.Ice;
import com.sokoban.model.tile.Portal;
import com.sokoban.model.tile.StoragePoint;
import com.sokoban.model.tile.Tile;
import com.sokoban.model.tile.Wall;
import com.sokoban.model.tile.mechanism.Door;
import com.sokoban.model.tile.mechanism.KeyTile;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Dibuja el tablero.
 *
 * <p>Es un {@link GameListener}: no consulta el modelo por su cuenta ni en un bucle,
 * se repinta cuando el nivel avisa que algo paso. Esa suscripcion es lo que permite
 * que el modelo no sepa que existe Swing.
 *
 * <p>El mapeo de baldosa a imagen vive aca y no en {@code Tile}: que dibujo le
 * corresponde a una pared es un problema de la vista, no del dominio.
 */
public class BoardView extends JPanel implements GameListener {

    private static final int CELL = SpriteCatalog.SIZE;
    private static final Color FOG = new Color(0x0A, 0x0C, 0x12);
    private static final Color BACKGROUND = new Color(0x1B, 0x1E, 0x26);

    private final transient Game game;
    private final transient SpriteCatalog sprites;

    public BoardView(Game game, SpriteCatalog sprites) {
        this.game = game;
        this.sprites = sprites;
        setBackground(BACKGROUND);
        setDoubleBuffered(true);
    }

    /** Ajusta el panel al tamano del nivel actual. */
    public void fitToLevel() {
        Level level = game.getCurrentLevel();
        if (level == null) {
            return;
        }
        Board board = level.getBoard();
        setPreferredSize(new Dimension(board.getCols() * CELL, board.getRows() * CELL));
        revalidate();
        repaint();
    }

    @Override
    public void onEvent(GameEvent event) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Level level = game.getCurrentLevel();
        if (level == null) {
            return;
        }
        Graphics2D g = (Graphics2D) graphics.create();
        // Es pixel art: no hay que suavizarlo al escalar.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        Board board = level.getBoard();
        VisibilityMap visibility = level.getVisibilityMap();
        int originX = Math.max(0, (getWidth() - board.getCols() * CELL) / 2);
        int originY = Math.max(0, (getHeight() - board.getRows() * CELL) / 2);
        for (Cell cell : board.getCells()) {
            Position position = cell.getPosition();
            int x = originX + position.col() * CELL;
            int y = originY + position.row() * CELL;
            if (!visibility.isVisible(position)) {
                g.setColor(FOG);
                g.fillRect(x, y, CELL, CELL);
                continue;
            }
            draw(g, tileSprite(cell.getTile()), x, y);
            drawOccupant(g, cell, x, y);
        }
        g.dispose();
    }

    private void drawOccupant(Graphics2D g, Cell cell, int x, int y) {
        Box stored = cell.getTile().getStoredBox();
        if (stored != null) {
            draw(g, SpriteKey.of(boxName(stored) + "_stored"), x, y);
        }
        if (cell.getOccupant() instanceof Player player) {
            draw(g, SpriteKey.player(player.getFacing()), x, y);
        }
        if (cell.getOccupant() instanceof Box box) {
            draw(g, SpriteKey.of(boxName(box)), x, y);
            if (box.getType() == BoxType.WEAK) {
                drawRemainingPushes(g, box, x, y);
            }
        }
    }

    /** La caja fragil muestra cuantos empujes le quedan. */
    private void drawRemainingPushes(Graphics2D g, Box box, int x, int y) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String text = Integer.toString(box.getRemainingPushes());
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        int width = g.getFontMetrics().stringWidth(text);
        int tx = x + (CELL - width) / 2;
        int ty = y + CELL / 2 + 7;
        g.setColor(Color.BLACK);
        g.drawString(text, tx + 1, ty + 1);
        g.setColor(Color.WHITE);
        g.drawString(text, tx, ty);
    }

    private void draw(Graphics2D g, SpriteKey key, int x, int y) {
        BufferedImage image = sprites.get(key);
        g.drawImage(image, x, y, CELL, CELL, null);
    }

    private String boxName(Box box) {
        return "box_" + box.getType().name().toLowerCase();
    }

    private SpriteKey tileSprite(Tile tile) {
        return switch (tile) {
            case Wall ignored -> SpriteKey.of("wall");
            case Empty ignored -> SpriteKey.of("empty");
            case Ice ignored -> SpriteKey.of("ice");
            case Portal ignored -> SpriteKey.of("portal");
            case KeyTile ignored -> SpriteKey.of("keytile");
            case Door door -> SpriteKey.of(door.isOpen() ? "door_open" : "door_closed");
            case StoragePoint point -> SpriteKey.of("storage_" + point.getType().name().toLowerCase());
            default -> SpriteKey.of("floor");
        };
    }
}
