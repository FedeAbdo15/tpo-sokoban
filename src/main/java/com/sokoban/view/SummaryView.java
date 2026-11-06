package com.sokoban.view;

import com.sokoban.model.game.LevelStats;
import com.sokoban.model.game.score.ScoreResult;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/** Resumen de fin de nivel que pide la consigna: movimientos, empujes, undos y puntaje. */
public class SummaryView extends JPanel {

    private static final Color BACKGROUND = new Color(0x1B, 0x1E, 0x26);

    private final JLabel title = big("", 34, new Color(0xE8, 0xA3, 0x3D));
    private final JLabel stars = big("", 30, new Color(0xF5, 0xD0, 0x4A));
    private final JLabel moves = line();
    private final JLabel pushes = line();
    private final JLabel undos = line();
    private final JLabel time = line();
    private final JLabel score = big("", 22, new Color(0xE6, 0xE9, 0xEF));
    private final JButton nextButton = new JButton();

    public SummaryView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        add(center(title));
        add(javax.swing.Box.createVerticalStrut(6));
        add(center(stars));
        add(javax.swing.Box.createVerticalStrut(24));
        for (JLabel label : new JLabel[]{moves, pushes, undos, time}) {
            add(center(label));
            add(javax.swing.Box.createVerticalStrut(6));
        }
        add(javax.swing.Box.createVerticalStrut(18));
        add(center(score));
        add(javax.swing.Box.createVerticalStrut(30));
        nextButton.setMaximumSize(new Dimension(260, 40));
        add(center(nextButton));
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public void show(String heading, String buttonText, LevelStats stats,
                     int remainingSeconds, ScoreResult result) {
        title.setText(heading);
        stars.setText(result == null ? "" : "*".repeat(Math.max(0, result.getStars())));
        moves.setText("Movimientos realizados: " + stats.getMoves());
        pushes.setText("Empujes efectuados: " + stats.getPushes());
        undos.setText("Usos del boton deshacer: " + stats.getUndos());
        time.setText("Tiempo restante: " + remainingSeconds + " s");
        score.setText(result == null ? "" : "Puntaje final: " + result.getScore());
        nextButton.setText(buttonText);
    }

    private JLabel line() {
        JLabel label = new JLabel();
        label.setForeground(new Color(0xC2, 0xC8, 0xD4));
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        return label;
    }

    private JLabel big(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
        return label;
    }

    private Component center(Component component) {
        ((javax.swing.JComponent) component).setAlignmentX(Component.CENTER_ALIGNMENT);
        return component;
    }
}
