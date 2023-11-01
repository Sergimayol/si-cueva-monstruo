package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.smm.betterswing.Section;
import org.smm.betterswing.Window;
import org.smm.betterswing.utils.DirectionAndPosition;

import agent.Robot;
import env.Environment;
import utils.Config;
import utils.FileLogger;
import utils.Helpers;

public class View {

    private Window window;
    private Map map;
    private Environment<Robot> env;
    private Robot robot;
    private JSplitPane splitPane;
    private int mapSize;
    private boolean stop;
    private int sleepTime;

    public View() {
        this.window = new Window(Config.VIEW_MAIN_WIN_CONFIG_PATH);
        this.window.initConfig();
        this.mapSize = 10;
        this.robot = new Robot();
        this.env = new Environment<>(this.mapSize);
        this.env.setAgent(this.robot);
        this.map = new Map(this.mapSize, this.env);
        this.stop = true;
        this.sleepTime = 500;
    }

    public void start() {
        FileLogger.info("[VIEW] Starting view...");
        this.window.addMenuBar(this.creatMenuBar());
        this.map.initMapTiles();
        this.initSplitPane();
        Section body = new Section();
        body.createFreeSection(this.createInitPage());
        this.window.addSection(body, DirectionAndPosition.POSITION_CENTER, "Main");
        this.window.start();
        FileLogger.info("[VIEW] View started");
    }

    private void runRobot() {
        while (!this.stop) {
            Tile tile = this.map.getTile(this.robot.getPosition().x, this.robot.getPosition().y);
            tile.setRobot(false);
            this.env.runNextMovement();
            final Point robotPos = this.env.getAgent().getPosition();
            tile = this.map.getTile(robotPos.x, robotPos.y);
            tile.setRobot(true);
            this.map.setTile(tile);
            this.map.paintComponent(this.map.getGraphics());
            this.map.revalidate();
            Helpers.wait(sleepTime);
        }
    }

    private JPanel sideBar() {
        FileLogger.info("[VIEW] Creating side bar...");
        JPanel sideBar = new JPanel();
        sideBar.setBackground(Color.WHITE);
        sideBar.setLayout(new GridLayout(4, 1));

        final String fontName = "Arial";
        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));
        iconPanel.setBackground(Color.WHITE);
        JLabel icon = new JLabel();
        icon.setBackground(Color.WHITE);
        icon.setIcon(Helpers.escalateImageIcon(Config.APP_UI_ICON_PATH, 90, 90));
        iconPanel.add(icon);
        JLabel logoText = new JLabel("Robot Inteligente");
        logoText.setFont(new Font(fontName, Font.ITALIC, 16));
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconPanel.add(logoText);
        logoPanel.add(iconPanel);
        logoPanel.add(Box.createVerticalStrut(10));

        // Actions panel
        JPanel actionsPanel = new JPanel();
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        JPanel state = new JPanel();
        state.setLayout(new BoxLayout(state, BoxLayout.Y_AXIS));
        JLabel stateLabel = new JLabel(" Iniciar robot ");
        stateLabel.setFont(new Font(fontName, Font.PLAIN, 14));
        stateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        state.add(stateLabel);

        JPanel numObstaculos = new JPanel();
        numObstaculos.setBackground(Color.WHITE);
        numObstaculos.setLayout(new BoxLayout(numObstaculos, BoxLayout.Y_AXIS));
        numObstaculos.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel numObstaculosLabel = new JLabel("Nº de obstáculos: " + this.map.getObstaclePositions().size());
        numObstaculosLabel.setFont(new Font(fontName, Font.PLAIN, 14));
        numObstaculos.add(numObstaculosLabel);

        JPanel roboPanel = new JPanel();
        roboPanel.setBackground(Color.WHITE);
        roboPanel.setLayout(new BoxLayout(roboPanel, BoxLayout.Y_AXIS));
        final int btnIconSize = 32;
        JButton roboButton = new JButton(Helpers.escalateImageIcon(Config.PAUSE_IMG_PATH, btnIconSize, btnIconSize));
        roboButton.setFont(new Font(fontName, Font.PLAIN, 14));
        roboButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        roboButton.setMaximumSize(new Dimension(
                (int) roboButton.getPreferredSize().getWidth(),
                (int) roboButton.getPreferredSize().getHeight()));

        roboButton.addActionListener(e -> {
            this.stop = !this.stop;
            final String action = this.stop ? "Stopping" : "Starting";
            FileLogger.info("[VIEW] " + action + " robot movement ...");
            if (this.env.getAgent().isDefaultPosition()) {
                JOptionPane.showMessageDialog(null, "No se ha encontrado un robot en el mapa", "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.stop = !this.stop;
                return;
            }
            final String btnICon = this.stop ? Config.PAUSE_IMG_PATH : Config.PLAY_IMG_PATH;
            roboButton.setIcon(Helpers.escalateImageIcon(btnICon, btnIconSize, btnIconSize));
            Thread.startVirtualThread(this::runRobot);
            numObstaculosLabel.setText("Nº de obstáculos: " + this.map.getObstaclePositions().size());
            final boolean applyGreen = !this.stop;
            state.setBackground(applyGreen ? Color.GREEN : Color.RED);
            stateLabel.setText(" " + (applyGreen ? "Ejecutando" : "Detenido"));
        });
        roboPanel.add(roboButton);

        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(roboPanel);
        actionsPanel.add(Box.createVerticalStrut(5));
        actionsPanel.add(state);
        actionsPanel.add(Box.createVerticalStrut(5));
        actionsPanel.add(numObstaculos);

        // Add size to the puzzle
        JPanel puzzleSizePanel = new JPanel();
        puzzleSizePanel.setBackground(Color.WHITE);
        puzzleSizePanel.setLayout(new BoxLayout(puzzleSizePanel, BoxLayout.Y_AXIS));
        JLabel puzzleSizeLabel = new JLabel("Tamaño del mapa:");
        puzzleSizeLabel.setFont(new Font(fontName, Font.PLAIN, 14));

        // Crear un modelo para el JSpinner con un rango de valores válidos
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(this.mapSize, 1, 50, 1);
        spinnerModel.addChangeListener(e -> {
            // Solo se puede cambiar el tamaño del mapa si el robot no está en ejecución
            this.stop = true;
            final String btnICon = this.stop ? Config.PAUSE_IMG_PATH : Config.PLAY_IMG_PATH;
            state.setBackground(!this.stop ? Color.GREEN : Color.RED);
            stateLabel.setText(" " + (!this.stop ? "Ejecutando" : "Detenido"));
            roboButton.setIcon(Helpers.escalateImageIcon(btnICon, btnIconSize, btnIconSize));
            this.mapSize = (int) spinnerModel.getValue();
            this.map.changeMapUIsize(this.mapSize, true);
            this.env = new Environment<>(this.mapSize);
            this.robot = new Robot();
            this.env.setAgent(this.robot);
            this.map.setEnvironment(this.env);
        });

        // Crear el JSpinner utilizando el modelo
        JSpinner puzzleSize = new JSpinner(spinnerModel);

        // Personalizar la apariencia del JSpinner
        puzzleSize.setFont(new Font(fontName, Font.PLAIN, 14));

        // Change the size of the puzzle to be the same as the label size
        puzzleSize.setMaximumSize(new Dimension(
                (int) puzzleSizeLabel.getPreferredSize().getWidth(),
                (int) puzzleSizeLabel.getPreferredSize().getHeight() + 15));

        // Put the same start location for both components
        puzzleSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        puzzleSize.setAlignmentX(Component.CENTER_ALIGNMENT);

        puzzleSizePanel.add(puzzleSizeLabel);
        puzzleSizePanel.add(puzzleSize);

        JPanel velocityPanel = new JPanel();
        velocityPanel.setBackground(Color.WHITE);
        velocityPanel.setLayout(new BoxLayout(velocityPanel, BoxLayout.Y_AXIS));
        JLabel velocityLabel = new JLabel("Velocidad:");
        velocityLabel.setFont(new Font(fontName, Font.PLAIN, 14));
        final int maxVal = 1500;
        final int minVal = 100;
        JSlider slider = new JSlider(SwingConstants.HORIZONTAL, minVal, maxVal, maxVal / 2);
        slider.addChangeListener(e -> View.this.sleepTime = slider.getValue());
        // Take in count the min and max values of the slider
        final int increment = (maxVal - minVal) / 2;
        // Set the labels to be painted on the slider
        slider.setPaintTicks(true);
        // Set the spacing for the labels on the slider
        slider.setMajorTickSpacing(increment);
        // Set the labels to be painted on the slider
        slider.setPaintLabels(true);
        // Add positions label in the slider
        slider.setLabelTable(slider.createStandardLabels(increment));
        slider.setMaximumSize(new Dimension(
                (int) puzzleSizeLabel.getPreferredSize().getWidth(),
                (int) puzzleSizeLabel.getPreferredSize().getHeight() + 30));
        // Put the same start location for both components
        velocityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);
        velocityPanel.add(velocityLabel);
        velocityPanel.add(slider);

        // Set the preferred size of the panels to be the same size as the logoPanel
        actionsPanel.setPreferredSize(new Dimension(
                (int) logoPanel.getPreferredSize().getWidth(),
                (int) logoPanel.getPreferredSize().getHeight()));
        puzzleSizePanel.setPreferredSize(new Dimension(
                (int) logoPanel.getPreferredSize().getWidth(),
                (int) logoPanel.getPreferredSize().getHeight()));
        velocityPanel.setPreferredSize(new Dimension(
                (int) logoPanel.getPreferredSize().getWidth(),
                (int) logoPanel.getPreferredSize().getHeight()));

        sideBar.add(logoPanel);
        sideBar.add(actionsPanel);
        sideBar.add(puzzleSizePanel);
        sideBar.add(velocityPanel);

        return sideBar;
    }

    private JPanel createInitPage() {
        FileLogger.info("[VIEW] Creating init page...");
        JPanel panel = new JPanel();
        final String fontName = "Arial";
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Práctica 1 - Robot con pasillos estrechos");
        label.setFont(new Font(fontName, Font.ITALIC, 22));
        JButton button = new JButton("Iniciar Simulación");
        button.addActionListener(e -> {
            this.splitPane.setRightComponent(this.sideBar());
            this.map.paintComponent(this.map.getGraphics());
            this.splitPane.setLeftComponent(this.map);
            Section body = new Section();
            body.createJSplitPaneSection(splitPane);
            this.window.updateSection(body, "Main", DirectionAndPosition.POSITION_CENTER);
        });
        JLabel consejoTile = new JLabel(
                "Ayuda: ");
        consejoTile.setFont(new Font(fontName, Font.ITALIC, 16));
        JLabel consejo1 = new JLabel(
                "1. Para añadir/quitar obstáculos, haz click izquierdo en el mapa");
        consejo1.setFont(new Font(fontName, Font.ITALIC, 14));
        JLabel consejo2 = new JLabel(
                "2. Para añadir/quitar el robot, haz click derecho en el mapa");
        consejo2.setFont(new Font(fontName, Font.ITALIC, 14));

        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        consejo1.setAlignmentX(Component.CENTER_ALIGNMENT);
        consejo2.setAlignmentX(Component.CENTER_ALIGNMENT);
        consejoTile.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(consejoTile);
        panel.add(Box.createVerticalStrut(10));
        panel.add(consejo1);
        panel.add(Box.createVerticalStrut(5));
        panel.add(consejo2);
        panel.add(Box.createVerticalStrut(10));
        panel.add(button);

        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(panel);
        mainPanel.add(Box.createVerticalStrut(10));

        return mainPanel;
    }

    public void stop() {
        FileLogger.info("[VIEW] Stopping view...");
        this.window.stop();
    }

    private void initSplitPane() {
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.setDividerLocation(0.9);
        this.splitPane.setResizeWeight(1.0);
        this.splitPane.setOneTouchExpandable(true);
        this.splitPane.setBorder(null);
    }

    private void showHelpDialog() {
        String helpMessage = "Click derecho: Añadir/Quitar Robot\nClick izquierdo: Añadir/Quitar Obstáculo";
        JOptionPane.showMessageDialog(null, helpMessage, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetMap() {
        this.stop = true;
        this.map.changeMapUIsize(this.mapSize, true);
        this.env = new Environment<>(this.mapSize);
        this.robot = new Robot();
        this.env.setAgent(this.robot);
        this.map.setEnvironment(this.env);
    }

    private JMenuBar creatMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Menú");
        JMenuItem exitMenuItem = new JMenuItem("Salir");
        JMenuItem helpMenuItem = new JMenuItem("Ayuda");
        JMenuItem resetMapItem = new JMenuItem("Reiniciar mapa");

        exitMenuItem.addActionListener(e -> this.stop());
        helpMenuItem.addActionListener(e -> this.showHelpDialog());
        resetMapItem.addActionListener(e -> this.resetMap());

        exitMenuItem.setToolTipText("Salir de la aplicación");
        helpMenuItem.setToolTipText("Mostrar ayuda");
        resetMapItem.setToolTipText("Reiniciar el mapa");

        fileMenu.add(resetMapItem);
        fileMenu.add(helpMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        return menuBar;
    }
}
