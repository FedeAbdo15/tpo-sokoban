package com.sokoban.view;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/** Pantalla inicial. */
public class MenuView extends JPanel {

    private static final Color BACKGROUND = new Color(0x1B, 0x1E, 0x26);

    private final JButton playButton = new JButton("Jugar desde el nivel 1");
    private final JButton continueButton = new JButton("Continuar");
    private final JButton exitButton = new JButton("Salir");
    private final JLabel subtitle = new JLabel();

    public MenuView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel title = new JLabel("SOKOBAN");
        title.setForeground(new Color(0xE8, 0xA3, 0x3D));
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        subtitle.setForeground(new Color(0x9A, 0xA2, 0xB2));
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        add(center(title));
        add(javax.swing.Box.createVerticalStrut(8));
        add(center(subtitle));
        add(javax.swing.Box.createVerticalStrut(36));
        for (JButton button : new JButton[]{playButton, continueButton, exitButton}) {
            button.setMaximumSize(new Dimension(260, 40));
            add(center(button));
            add(javax.swing.Box.createVerticalStrut(12));
        }
    }

    public void showProgress(int lastUnlockedLevel, int levelCount) {
        continueButton.setEnabled(lastUnlockedLevel > 1);
        continueButton.setText("Continuar desde el nivel " + lastUnlockedLevel);
        subtitle.setText("Empuja cada caja a su destino. " + levelCount + " niveles.");
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getContinueButton() {
        return continueButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    private Component center(Component component) {
        ((javax.swing.JComponent) component).setAlignmentX(Component.CENTER_ALIGNMENT);
        return component;
    }
}
