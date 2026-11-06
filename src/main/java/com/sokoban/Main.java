package com.sokoban;

import com.sokoban.controller.GameClock;
import com.sokoban.controller.HudController;
import com.sokoban.controller.InputController;
import com.sokoban.exception.InvalidLevelException;
import com.sokoban.factory.LevelLoader;
import com.sokoban.model.game.Game;
import com.sokoban.model.game.Level;
import com.sokoban.model.game.state.GameCompletedState;
import com.sokoban.port.ProgressRepository;
import com.sokoban.service.FileProgressRepository;
import com.sokoban.service.ProgressSaver;
import com.sokoban.service.SoundService;
import com.sokoban.view.GameWindow;
import com.sokoban.view.Screen;
import com.sokoban.view.SpriteCatalog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.nio.file.Path;

/**
 * Punto de entrada: arma el modelo, la vista y los controladores, y los conecta.
 *
 * <p>Es el unico lugar del proyecto donde se cruzan las tres capas. Ninguna clase de
 * {@code model}, {@code factory} ni {@code port} importa {@code javax.swing}: la vista
 * se entera de los cambios solo a traves de {@code GameListener}.
 */
public final class Main {

    private static final int LEVEL_COUNT = 5;
    private static final String LEVEL_PATH = "/levels/nivel%02d.txt";
    private static final Path PROGRESS_FILE = Path.of(System.getProperty("user.home"), ".sokoban-progreso.txt");

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
    }

    private static void start() {
        useSystemLookAndFeel();
        LevelLoader loader = new LevelLoader();
        if (!levelsAreValid(loader)) {
            return;
        }

        Game game = new Game(number -> load(loader, number), LEVEL_COUNT);
        ProgressRepository progress = new FileProgressRepository(PROGRESS_FILE);
        GameWindow window = new GameWindow(game, new SpriteCatalog());

        // Observers que se reenganchan solos a cada nivel que se carga.
        game.addLevelListener(window.getBoardView());
        game.addLevelListener(window.getHudView());
        game.addLevelListener(new SoundService());
        game.addLevelListener(new ProgressSaver(game, progress));

        GameClock clock = new GameClock(game, window::refresh);
        new HudController(game, window::refresh).install(window.getHudView());

        InputController input = new InputController(game, window::refresh);
        input.install(window.getRoot());
        input.bind(window.getRoot(), "U", game::undo);
        input.bind(window.getRoot(), "R", game::restart);
        input.bind(window.getRoot(), "ESCAPE", () -> {
            clock.stop();
            window.show(Screen.MENU);
        });

        wireMenu(window, game, progress, clock);
        wireSummary(window, game);

        window.getMenuView().showProgress(progress.load().getLastUnlockedLevel(), LEVEL_COUNT);
        window.setMinimumSize(new java.awt.Dimension(860, 420));
        window.setSize(900, 620);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private static void wireMenu(GameWindow window, Game game, ProgressRepository progress, GameClock clock) {
        window.getMenuView().getPlayButton().addActionListener(event -> {
            game.start();
            window.showLevel();
            clock.start();
        });
        window.getMenuView().getContinueButton().addActionListener(event -> {
            game.start();
            game.loadLevel(progress.load().getLastUnlockedLevel());
            window.showLevel();
            clock.start();
        });
        window.getMenuView().getExitButton().addActionListener(event -> System.exit(0));
    }

    private static void wireSummary(GameWindow window, Game game) {
        window.getSummaryView().getNextButton().addActionListener(event -> {
            if (game.getCurrentLevel().getLossReason() != null) {
                game.restart();          // nivel perdido: se reintenta el mismo
            } else {
                game.nextLevel();
            }
            if (game.getState() instanceof GameCompletedState) {
                window.showGameCompleted();
                window.getSummaryView().getNextButton().addActionListener(back ->
                        window.show(Screen.MENU));
                return;
            }
            window.showLevel();
        });
    }

    /** Se validan los 5 niveles al arrancar, para no fallar en medio de la partida. */
    private static boolean levelsAreValid(LevelLoader loader) {
        for (int number = 1; number <= LEVEL_COUNT; number++) {
            try {
                loader.load(String.format(LEVEL_PATH, number));
            } catch (InvalidLevelException e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo cargar el nivel " + number + ":\n" + e.getMessage(),
                        "Sokoban", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Adapta el cargador a la fabrica que espera {@code Game}. El nivel ya se valido al
     * arrancar, asi que si falla aca es un error de programacion y no del archivo.
     */
    private static Level load(LevelLoader loader, int number) {
        try {
            return loader.load(String.format(LEVEL_PATH, number));
        } catch (InvalidLevelException e) {
            throw new IllegalStateException("Nivel " + number + " invalido: " + e.getMessage(), e);
        }
    }

    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Si el look and feel del sistema no esta disponible se usa el de Swing.
        }
    }
}
