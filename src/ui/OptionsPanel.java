package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class OptionsPanel extends JPanel {

    private final int DIM_ICON = 60;
    private final int DIM_ROBOT = 120;
    private final int ANCHO = 250;
    private final int ALTO = 20;
    private static final Font titleFont = new Font("Calibri", Font.BOLD, 16);

    private String[] entitiesName;
    private Class[] entitiesClass = null;
    private Constructor[] entitiesConstructor = null;
    private final String baseImagesPath = "./assets/images/";
    private final String extension = ".png";

    private final MonstersCaveGui gui;
    private JSlider speedSlider;
    private JSlider nExplorersSlider;
    private int currentSelectedEntityIndex = 0;
    private JButton startButton;
    private JButton stopButton;
    private JButton stepButton;
    private boolean firstStep = true;

    private final Dimension dimensionInputs = new Dimension(145, 30);

    private ButtonGroup groupbtn;

    private ArrayList<JButton> botones = new ArrayList<>();
    private JFormattedTextField inputDimsTablero;
    private int dimsTableroPrevias;

    @SuppressWarnings("unchecked")
    public OptionsPanel(int n, MonstersCaveGui gui) {
        try {
            this.entitiesClass = new Class[] {
                    Class.forName("ui.entities.Hole"),
                    Class.forName("ui.entities.Monster"),
                    Class.forName("ui.entities.Treasure")
            };

            this.entitiesName = new String[this.entitiesClass.length];
            for (int i = 0; i < entitiesName.length; i++) {
                entitiesName[i] = (String) this.entitiesClass[i].getField("IMAGENAME").get(null);
            }

            this.entitiesConstructor = new Constructor[this.entitiesClass.length];
            for (int i = 0; i < entitiesConstructor.length; i++) {
                entitiesConstructor[i] = this.entitiesClass[i].getConstructor(int.class, int.class);
            }

        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException | NoSuchMethodException ex) {
            Logger.getLogger(OptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.gui = gui;
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, Color.black),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.initComponents();

        inputDimsTablero.setText(Integer.toString(n));
        dimsTableroPrevias = n;

    }

    public Class<?>[] getEntityClasses() {
        return this.entitiesClass;
    }

    public void setText(String s) {
        inputDimsTablero.setText(s);
    }

    private void initComponents() {
        groupbtn = new ButtonGroup();
        initInputDimTablero();
        initInputs();

        initExplorerSelector();
    }

    private void initInputDimTablero() {
        JPanel panelBotones = new JPanel();

        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

        panelBotones.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.black, 2),
                                "Resolución del entorno (NxN)"),
                        BorderFactory.createEmptyBorder(10, 5, 15, 5)));
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

        panelBotones.add(crearInput("NxN", 2, -1));

        this.add(panelBotones);
    }

    private void initInputs() {

        JPanel entradas = new JPanel();

        entradas.setLayout(new BoxLayout(entradas, BoxLayout.Y_AXIS));

        entradas.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.black, 2),
                                "Elementos"),
                        BorderFactory.createEmptyBorder(10, 5, 15, 5)));

        int nPiezas = entitiesName.length;
        for (int i = 0; i < nPiezas; i++) {
            String nombrePieza = entitiesName[i];
            entradas.add(crearInput(nombrePieza, 2, i));
            if (i < nPiezas - 1) {
                entradas.add(Box.createRigidArea(new Dimension(5, 20)));
            }
        }
        this.add(entradas);
    }

    private JPanel crearInput(String etiq, int lim, int pos) {
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new BoxLayout(panelEntrada, BoxLayout.X_AXIS));

        if (pos >= 0) {
            panelEntrada.add(Box.createHorizontalGlue());
        }

        panelEntrada.add(CrearEtiq(etiq));

        panelEntrada.add(Box.createRigidArea(new Dimension(10, 5)));

        if (pos >= 0) {

            JRadioButton jr = new JRadioButton();
            jr.setIcon(new ImageIcon(new ImageIcon(baseImagesPath + "radioicon" + extension).getImage()
                    .getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
            jr.setRolloverIcon(new ImageIcon(new ImageIcon(baseImagesPath + "radiorollovericon" + extension).getImage()
                    .getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
            jr.setSelectedIcon(new ImageIcon(new ImageIcon(baseImagesPath + "radioselectedicon" + extension).getImage()
                    .getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
            jr.setRolloverSelectedIcon(
                    new ImageIcon(new ImageIcon(baseImagesPath + "radiorolloverselectedicon" + extension).getImage()
                            .getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
            jr.setActionCommand(Integer.toString(pos));
            jr.addActionListener(getChangeObstacleActionListener());
            this.groupbtn.add(jr);
            panelEntrada.add(jr);

            if (pos == 0) {
                jr.doClick();
            }
        }

        panelEntrada.add(Box.createHorizontalGlue());

        if (pos < 0) {
            JFormattedTextField entradaTexto = new JFormattedTextField();
            entradaTexto.setName(etiq);
            inputDimsTablero = entradaTexto;

            entradaTexto.addActionListener(e -> this.changeCaveSize());

            TextPrompt placeholder = new TextPrompt(etiq, entradaTexto);

            entradaTexto.setPreferredSize(dimensionInputs);
            entradaTexto.setMaximumSize(dimensionInputs);
            entradaTexto.setMargin(new Insets(0, 5, 0, 0));

            entradaTexto.addKeyListener(new InputListener(entradaTexto, lim));
            entradaTexto.setActionCommand(etiq.toLowerCase());

            placeholder.changeAlpha(0.75f);
            placeholder.changeStyle(Font.ITALIC);
            panelEntrada.add(entradaTexto);

        }

        return panelEntrada;
    }

    private void initExplorerSelector() {
        JPanel panelExplorers = new JPanel();

        ActionListener buttonAction = getControlActionListener();

        panelExplorers.setLayout(new BoxLayout(panelExplorers, BoxLayout.Y_AXIS));

        panelExplorers.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.black, 2),
                                "Controlador de exploradores"),
                        BorderFactory.createEmptyBorder(10, 5, 15, 5)));
        panelExplorers.setLayout(new BoxLayout(panelExplorers, BoxLayout.Y_AXIS));

        // ----------------------------------------------------------------------
        // ------------------------ MOVEMENT CONTROL ----------------------------
        // ----------------------------------------------------------------------
        // --- AUTO ---
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new GridLayout(2, 1, 0, -15));

        JLabel autoTitle = new JLabel("Control automático");
        autoTitle.setFont(titleFont);
        autoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        autoPanel.add(autoTitle);

        JPanel autoButtons = new JPanel();
        autoButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        autoButtons.setLayout(new BoxLayout(autoButtons, BoxLayout.X_AXIS));

        startButton = createButton("Arrancar", buttonAction);
        stopButton = createButton("Parar", buttonAction, false);

        autoButtons.add(startButton);
        autoButtons.add(Box.createRigidArea(new Dimension(10, 1)));
        autoButtons.add(stopButton);

        autoPanel.add(autoButtons);

        // --- MANUAL ---
        JPanel manualPanel = new JPanel();
        manualPanel.setLayout(new GridLayout(2, 1, 0, -15));

        JPanel manualButtons = new JPanel();
        manualButtons.setLayout(new BoxLayout(manualButtons, BoxLayout.X_AXIS));

        JLabel manualTitle = new JLabel("Control manual");
        manualTitle.setFont(titleFont);
        manualTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        manualPanel.add(manualTitle);

        stepButton = createButton("Paso", buttonAction);

        manualButtons.add(stepButton);

        JButton resetButton = createButton("Reset", e -> changeNumberOfExplorers());

        resetButton.setBackground(new Color(227, 170, 170));

        manualButtons.add(Box.createHorizontalGlue());

        manualButtons.add(resetButton);

        manualPanel.add(manualButtons);

        panelExplorers.add(autoPanel);
        panelExplorers.add(manualPanel);

        // ----------------------------------------------------------------------
        // -------------------------- SPEED SLIDER ------------------------------
        // ----------------------------------------------------------------------
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new GridLayout(2, 1, 0, -10));

        JLabel speedTitle = new JLabel("Velocidad");
        speedTitle.setFont(titleFont);
        speedTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        speedPanel.add(speedTitle);

        JPanel speedSliderPanel = new JPanel();
        speedSliderPanel.setLayout(new GridLayout());

        speedSlider = new JSlider(1, 4, 1) {
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this));
            }
        };

        speedSlider.setValue(2);
        speedSlider.setPaintLabels(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setForeground(Color.BLACK);

        Hashtable<Integer, JLabel> labelsSpeed = new Hashtable<>();
        labelsSpeed.put(Integer.valueOf(1), new JLabel("x0.5"));
        labelsSpeed.put(Integer.valueOf(2), new JLabel("x1"));
        labelsSpeed.put(Integer.valueOf(3), new JLabel("x2"));
        labelsSpeed.put(Integer.valueOf(4), new JLabel("x4"));
        speedSlider.setLabelTable(labelsSpeed);

        speedSlider.addChangeListener(e -> updateRobotSpeedFactor());
        speedSliderPanel.add(speedSlider);

        speedPanel.add(speedSliderPanel);
        panelExplorers.add(speedPanel);

        // ----------------------------------------------------------------------
        // ------------------------ N EXPLORERS SLIDER --------------------------
        // ----------------------------------------------------------------------
        JPanel explorersPanel = new JPanel();
        explorersPanel.setLayout(new BoxLayout(explorersPanel, BoxLayout.Y_AXIS));

        JLabel explorersTitle = new JLabel("Nº exploradores");
        explorersTitle.setFont(titleFont);
        explorersPanel.add(explorersTitle);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridLayout());

        nExplorersSlider = new JSlider(1, 4, 1) {
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this));
            }
        };

        nExplorersSlider.setValue(1);
        nExplorersSlider.setOrientation(SwingConstants.VERTICAL);

        nExplorersSlider.setPaintLabels(true);
        nExplorersSlider.setPaintTicks(true);
        nExplorersSlider.setMajorTickSpacing(1);
        nExplorersSlider.setForeground(Color.BLACK);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        JLabel label1 = new JLabel("1");
        label1.setIcon(new ImageIcon("./assets/images/agents/explorer1_left.png"));
        JLabel label2 = new JLabel("2");
        label2.setIcon(new ImageIcon("./assets/images/agents/explorer2_left.png"));
        JLabel label3 = new JLabel("3");
        label3.setIcon(new ImageIcon("./assets/images/agents/explorer3_left.png"));
        JLabel label4 = new JLabel("4");
        label4.setIcon(new ImageIcon("./assets/images/agents/explorer4_left.png"));
        labels.put(Integer.valueOf(1), label1);
        labels.put(Integer.valueOf(2), label2);
        labels.put(Integer.valueOf(3), label3);
        labels.put(Integer.valueOf(4), label4);
        nExplorersSlider.setLabelTable(labels);

        nExplorersSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                changeNumberOfExplorers();
            }
        });

        panelExplorers.add(Box.createRigidArea(new Dimension(1, 10)));

        sliderPanel.add(nExplorersSlider);
        explorersPanel.add(sliderPanel);
        panelExplorers.add(explorersPanel);

        // ----------------------------------------------------------------------
        panelExplorers.add(Box.createVerticalGlue());

        this.add(panelExplorers);
    }

    private JLabel CrearEtiq(String str) {
        JLabel lab = new JLabel();
        ImageIcon baseImg = new ImageIcon(baseImagesPath + str.toLowerCase() + extension);
        lab.setIcon(new ImageIcon(baseImg.getImage().getScaledInstance(DIM_ICON, DIM_ICON, Image.SCALE_SMOOTH)));
        return lab;
    }

    private JButton createButton(String str, ActionListener l) {
        return createButton(str, l, true);
    }

    private JButton createButton(String str, ActionListener l, boolean enabled) {
        JButton button = new JButton(str);
        button.setBackground(Color.white);
        button.setEnabled(enabled);
        button.addActionListener(l);
        return button;
    }

    // Cambiamos el tamaño que tendrá el panel cuando se aplique el pack
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ANCHO, ALTO);
    }

    public void updateRobotSpeedFactor() {
        gui.setExplorerDisplayerSpeedFactor(this.getSpeedFactor());
    }

    @SuppressWarnings("unchecked")
    public double getSpeedFactor() {
        Hashtable<Integer, JLabel> labelTable = (Hashtable<Integer, JLabel>) speedSlider.getLabelTable();
        Integer value = speedSlider.getValue();
        JLabel label = labelTable.get(value);
        return Double.valueOf(label.getText().replace("x", ""));
    }

    private void changeNumberOfExplorers() {
        parar();
        int value = nExplorersSlider.getValue();
        gui.setNumberOfExplorers(value);
    }

    public int getNumberOfExplorers() {
        return this.nExplorersSlider.getValue();
    }

    public Constructor getCurrentEntityConstructor() {
        return this.entitiesConstructor[this.currentSelectedEntityIndex];
    }

    class InputListener implements KeyListener {

        private JFormattedTextField input;
        private int lim;

        public InputListener(JFormattedTextField in, int lim) {
            this.input = in;
            this.lim = lim;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE) || input.getText().length() == lim) {
                e.consume();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Do nothing
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Do nothing
        }

    }

    private void changeCaveSize() {
        String text = inputDimsTablero.getText();
        if (!text.equals("")) {
            int n = Integer.parseInt(text);
            if (n > 0) {
                this.dimsTableroPrevias = n;
                parar();
                this.resetFirstStep();
                this.gui.addNewCave(n);
            }
        }
    }

    public void lock() {
        this.internalLock(false);
    }

    public void unlock() {
        this.internalLock(true);
    }

    private void internalLock(boolean estado) {
        inputDimsTablero.setEnabled(estado);
    }

    private ActionListener getChangeObstacleActionListener() {
        return (ActionEvent e) -> currentSelectedEntityIndex = Integer.parseInt(e.getActionCommand());
    }

    private ActionListener getControlActionListener() {
        return (ActionEvent e) -> {
            switch (e.getActionCommand()) {
                case "Arrancar" -> arrancar();
                case "Parar" -> parar();
                case "Paso" -> paso();
                default -> {
                    // Do nothing
                }
            }
        };
    }

    private void arrancar() {
        lockCave();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        stepButton.setEnabled(false);
        this.gui.letExplorersGo(true, false);
    }

    private void parar() {
        this.gui.letExplorersGo(false, false);
    }

    public void stop() {
        parar();
    }

    private void paso() {
        lockCave();
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        stepButton.setEnabled(false);
        this.gui.letExplorersGo(true, true);
    }

    private void lockCave() {
        if (firstStep) {
            this.gui.lockCave();
            firstStep = false;
        }
    }

    public void animationFinished() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        stepButton.setEnabled(true);
    }

    public void resetFirstStep() {
        firstStep = true;
    }

}
