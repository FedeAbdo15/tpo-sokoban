package com.sokoban.view;

import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.LevelStats;
import com.sokoban.model.game.event.GameEvent;
import com.sokoban.model.game.event.GameListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Barra superior con los contadores y los botones que pide la consigna.
 *
 * <p>Tambien es {@link GameListener}: se refresca con los eventos del nivel, y los
 * controladores la refrescan a mano despues de deshacer o reiniciar.
 *
 * <p>Usa BorderLayout con los contadores a la izquierda y los botones a la derecha,
 * para que al achicar la ventana los botones no aplasten los textos.
 */
public class HudView extends JPanel implements GameListener {

    private static final Color BACKGROUND = new Color(0x23, 0x27, 0x31);
    private static final Color TEXT = new Color(0xE6, 0xE9, 0xEF);
    private static final Color DIM = new Color(0x8A, 0x93, 0xA6);

    private final transient Game game;
    private final JLabel levelLabel = label(TEXT);
    private final JLabel movesLabel = label(TEXT);
    private final JLabel pushesLabel = label(TEXT);
    private final JLabel timeLabel = label(TEXT);
    private final JLabel undosLabel = label(TEXT);
    private final JButton undoButton = new JButton("Deshacer");
    private final JButton restartButton = new JButton("Reiniciar");

    public HudView(Game game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        undoButton.setToolTipText("Retrocede 5 movimientos. Hasta 3 usos seguidos. Tecla U");
        restartButton.setToolTipText("Vuelve a empezar el nivel actual. Tecla R");

        JPanel counters = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        counters.setOpaque(false);
        add(counters, BorderLayout.CENTER);
        for (JLabel label : new JLabel[]{levelLabel, movesLabel, pushesLabel, timeLabel, undosLabel}) {
            if (counters.getComponentCount() > 0) {
                counters.add(separator());
            }
            counters.add(label);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(undoButton);
        buttons.add(restartButton);
        add(buttons, BorderLayout.EAST);

        refresh();
    }

    public JButton getUndoButton() {
        return undoButton;
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    @Override
    public void onEvent(GameEvent event) {
        refresh();
    }

    public void refresh() {
        Level level = game.getCurrentLevel();
        if (level == null) {
            return;
        }
        LevelStats stats = level.getStats();
        levelLabel.setText("Nivel " + game.getCurrentLevelNumber() + "/" + game.getLevelCount());
        levelLabel.setToolTipText(level.getName());
        movesLabel.setText("Movimientos: " + stats.getMoves());
        pushesLabel.setText("Empujes: " + stats.getPushes());
        timeLabel.setText("Tiempo: " + format(level.getRemainingSeconds()));
        undosLabel.setText("Deshacer: " + level.getRemainingUndos());
        undoButton.setEnabled(level.getRemainingUndos() > 0);
    }

    private String format(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    private JLabel label(Color color) {
        JLabel label = new JLabel();
        label.setForeground(color);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        return label;
    }

    private Component separator() {
        JLabel dot = new JLabel("  ·  ");
        dot.setForeground(DIM);
        dot.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        return dot;
    }
}
