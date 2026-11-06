package com.sokoban.view;

import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.score.ScoreCalculator;
import com.sokoban.model.game.score.StandardScoreCalculator;
import com.sokoban.model.game.state.GameCompletedState;
import com.sokoban.model.game.state.LostState;
import com.sokoban.model.game.state.WonState;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

/**
 * Ventana principal. Es la fachada de la vista: arma las tres pantallas en un
 * CardLayout y decide cual se muestra segun el estado del juego.
 */
public class GameWindow extends JFrame {

    private final transient Game game;
    private final transient ScoreCalculator scoreCalculator = new StandardScoreCalculator();

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final MenuView menuView = new MenuView();
    private final SummaryView summaryView = new SummaryView();
    private final BoardView boardView;
    private final HudView hudView;
    private Screen currentScreen = Screen.MENU;

    public GameWindow(Game game, SpriteCatalog sprites) {
        super("Sokoban");
        this.game = game;
        this.boardView = new BoardView(game, sprites);
        this.hudView = new HudView(game);

        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(hudView, BorderLayout.NORTH);
        gamePanel.add(boardView, BorderLayout.CENTER);

        root.add(menuView, Screen.MENU.name());
        root.add(gamePanel, Screen.GAME.name());
        root.add(summaryView, Screen.SUMMARY.name());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(root);
        show(Screen.MENU);
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public HudView getHudView() {
        return hudView;
    }

    public MenuView getMenuView() {
        return menuView;
    }

    public SummaryView getSummaryView() {
        return summaryView;
    }

    public JPanel getRoot() {
        return root;
    }

    public void show(Screen screen) {
        currentScreen = screen;
        cards.show(root, screen.name());
        root.requestFocusInWindow();
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    /** Entra al tablero y lo ajusta al nivel actual. */
    public void showLevel() {
        // No se hace pack(): el tablero se dibuja centrado, asi la ventana no salta
        // de tamano cada vez que se cambia de nivel.
        boardView.fitToLevel();
        hudView.refresh();
        show(Screen.GAME);
    }

    /**
     * Punto unico de refresco: lo llaman los controladores despues de cada accion.
     * Si el nivel termino, pasa al resumen.
     */
    public void refresh() {
        hudView.refresh();
        boardView.repaint();
        // Solo se salta al resumen si estabamos jugando: si el jugador volvio al menu, no.
        if (currentScreen == Screen.GAME
                && (game.getState() instanceof WonState || game.getState() instanceof LostState)) {
            showSummary();
        }
    }

    private void showSummary() {
        Level level = game.getCurrentLevel();
        boolean won = game.getState() instanceof WonState;
        summaryView.show(
                won ? "Nivel completado" : "Nivel perdido",
                won ? nextButtonText() : "Reintentar",
                level.getStats(),
                level.getRemainingSeconds(),
                won ? scoreCalculator.calculate(level.getStats(), level.getRemainingSeconds(),
                        level.getBoard().countStoredBoxes(), level.getOpenedDoors(),
                        level.getTargetScore()) : null);
        show(Screen.SUMMARY);
    }

    private String nextButtonText() {
        return game.hasNextLevel() ? "Siguiente nivel" : "Terminar";
    }

    /** Pantalla final, cuando ya no quedan niveles. */
    public void showGameCompleted() {
        if (!(game.getState() instanceof GameCompletedState)) {
            return;
        }
        summaryView.show("Juego completado", "Volver al menu",
                game.getCurrentLevel().getStats(),
                game.getCurrentLevel().getRemainingSeconds(), null);
        show(Screen.SUMMARY);
    }
}
