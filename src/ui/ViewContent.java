package ui;

import agent.Explorer;
import environment.Environment;
import utils.Helpers;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ViewContent extends JFrame {

    private static final int INITIAL_SIZE = 15;

    private Cave cave;
    private OptionsPanel options;
    private Environment<Explorer> env;
    private transient ExecutorService environmentExecutor = Executors.newSingleThreadExecutor();
    private transient ExecutorService animationExecutor = Executors.newFixedThreadPool(4);
    private ExplorerDisplayer[] explorerDisplayers;
    private FeaturesPanel featuresPanel;

    public ViewContent() {

        super("Monsters' cave");
        this.setIconImage(Helpers.readImage("./assets/icon.png"));

        this.setLayout(new BorderLayout());

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initComponents();
    }

    public void showGui() {
        this.pack();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(ViewContent.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initComponents() {

        featuresPanel = new FeaturesPanel(this);
        this.add(featuresPanel, BorderLayout.SOUTH);

        this.options = new OptionsPanel(INITIAL_SIZE, this);
        this.add(this.options, BorderLayout.WEST);

        this.addNewCave(INITIAL_SIZE);

    }

    public void setInfo(CaveInfo info) {
        addNewCave(info);
        options.setText(Integer.toString(env.getSize()));
        this.cave.paintAll();
    }

    public void addNewCave(CaveInfo info) {

        if (this.cave != null) {
            this.remove(this.cave);
        }

        this.env = new Environment<>(info.getTilesData(), this.options.getNumberOfExplorers(), this);

        int nAgents = this.options.getNumberOfExplorers();
        Explorer[] explorers = new Explorer[nAgents];
        for (int i = 0; i < explorers.length; i++) {
            explorers[i] = new Explorer(i, this.env, this.featuresPanel.canShoot(), this.featuresPanel.canPutBridge());
        }

        this.env.setAgents(explorers);

        Object[] agents = this.env.getAgents();

        // Create explorers array
        this.explorerDisplayers = createExplorerDisplayers(agents);

        this.cave = new Cave(info.getTiles(), this, env, explorerDisplayers);
        this.add(cave, BorderLayout.CENTER);

        this.options.updateRobotSpeedFactor();

        this.revalidate();
        this.repaint();

    }

    public void addNewCave(int n) {

        if (this.cave != null) {
            this.remove(this.cave);
        }

        int nAgents = this.options.getNumberOfExplorers();

        this.env = new Environment<>(n, nAgents, this);

        Explorer[] explorers = new Explorer[nAgents];
        for (int i = 0; i < explorers.length; i++) {
            explorers[i] = new Explorer(i, this.env, this.featuresPanel.canShoot(), this.featuresPanel.canPutBridge());
        }

        this.env.setAgents(explorers);

        Object[] agents = this.env.getAgents();

        this.explorerDisplayers = createExplorerDisplayers(agents);

        this.cave = new Cave(n, this, env, explorerDisplayers);
        this.add(cave, BorderLayout.CENTER);

        this.options.updateRobotSpeedFactor();

        this.revalidate();
        this.repaint();

    }

    private void runAnimationGoBack(boolean step) {
        List<Future<Boolean>> futures = null;
        while (true) {
            // Wait for animation to finish
            if (futures != null) {
                try {
                    boolean robotActive = true;
                    for (Future<Boolean> future : futures) {
                        robotActive = robotActive && future.get();
                    }
                    if (!robotActive || step) {
                        this.options.animationFinished();
                        return;
                    }

                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ViewContent.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.currentThread().interrupt();
                }
            }

            // Run iteration of the environment
            this.env.runIteration();

            List<AnimationExecutor> tasks = new ArrayList<>();
            for (int i = 0; i < this.env.getNAgents(); i++) {
                // Execute animation for each agent
                tasks.add(new AnimationExecutor(i));
            }

            try {
                futures = animationExecutor.invokeAll(tasks);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }

    public void letExplorersGo(boolean canGo, boolean step) {
        this.cave.setExplorersActive(canGo);
        if (canGo) {
            environmentExecutor.execute(() -> runAnimationGoBack(step));
        }
    }

    public void putBridge(Point holePos) {
        this.cave.putBridge(holePos);
    }

    public boolean thereAreTreasures() {
        return this.cave.thereAreTreasures();
    }

    public void finishRound() {
        this.options.stop();
        this.env.resetAgentsFinished();

        (new Result(this, this.env.getAgents(), getExplorerImages(explorerDisplayers))).showResults();
    }

    private class AnimationExecutor implements Callable<Boolean> {

        private int explorerId;

        public AnimationExecutor(int explorerId) {
            this.explorerId = explorerId;
        }

        @Override
        public Boolean call() throws Exception {
            return cave.moveExplorer(explorerId);
        }

    }

    public void setExplorerDisplayerSpeedFactor(double speedFactor) {
        this.cave.setExplorerDisplayerSpeedFactor(speedFactor);
    }

    public void setNumberOfExplorers(int nAgents) {

        unlockCave();

        this.env.setNumberOfAgents(nAgents);

        Explorer[] explorers = new Explorer[nAgents];
        for (int i = 0; i < explorers.length; i++) {
            explorers[i] = new Explorer(i, this.env, this.featuresPanel.canShoot(), this.featuresPanel.canPutBridge());
        }

        this.env.setAgents(explorers);

        Object[] agents = this.env.getAgents();

        this.explorerDisplayers = createExplorerDisplayers(agents);

        this.cave.setExplorerDisplayers(explorerDisplayers);

    }

    public void saveMap() {
        String value = JOptionPane.showInputDialog(this, "Selecciona el nombre del archivo", "Guardar archivo",
                JOptionPane.QUESTION_MESSAGE);
        if (value == null || value.equals("")) {
            return;
        }
        CaveInfo info = new CaveInfo(this.env.getSnap(), this.cave.getTiles());
        info.saveCave(value);
    }

    public void loadMap() {

        JFileChooser fileChooser = new JFileChooser("test_maps/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Maps of Cave", "map"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int value = fileChooser.showOpenDialog(this);
        if (value == JFileChooser.APPROVE_OPTION) {
            CaveInfo info;
            info = CaveInfo.useCave(fileChooser.getSelectedFile().getName());
            this.setInfo(info);
        }

    }

    public void resetCave() {
        this.options.stop();
        this.setNumberOfExplorers(this.options.getNumberOfExplorers());
        this.cave.resetAll();
    }

    public Constructor getCurrentEntityConstructor() {
        return this.options.getCurrentEntityConstructor();
    }

    public Class[] getEntityClasses() {
        return this.options.getEntityClasses();
    }

    public ExplorerDisplayer[] createExplorerDisplayers(Object[] agents) {
        ExplorerDisplayer[] explorerDisplayers = new ExplorerDisplayer[agents.length];
        for (int i = 0; i < explorerDisplayers.length; i++) {
            explorerDisplayers[i] = new ExplorerDisplayer((Explorer) agents[i], i);
            explorerDisplayers[i].setSpeedFactor(this.options.getSpeedFactor());
        }
        return explorerDisplayers;
    }

    private BufferedImage[] getExplorerImages(ExplorerDisplayer[] expDisplayers) {
        BufferedImage[] imgs = new BufferedImage[expDisplayers.length];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = expDisplayers[i].getImageLeft();

        }
        return imgs;
    }

    public void takeTreasure(Point position) {
        this.cave.takeTreasure(position);
    }

    public void killMonster(Point monsterPos) {
        this.cave.killMonster(monsterPos);
    }

    public void lockCave() {
        this.env.snapMap();
        this.cave.lockCave();
        this.repaint();
    }

    public void unlockCave() {
        this.cave.unlockCave();
        this.cave.resetEntities();
        this.env.restoreMapFromSnap();
        this.options.resetFirstStep();
    }

    public boolean caveIsLocked() {
        return this.cave.isLocked();
    }

    public void setExplorersCanShoot(boolean canShoot) {
        for (Explorer explorer : this.env.getAgents()) {
            explorer.setCanShoot(canShoot);
        }
    }

    public void setExplorersCanPutBridge(boolean canPutBridge) {
        for (Explorer explorer : this.env.getAgents()) {
            explorer.setCanPutBridge(canPutBridge);
        }
    }

}
