package environment;

import agent.Explorer;
import entities.CaveEditor;
import gui.MonstersCaveGui;
import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;

public class Environment<T> implements Serializable {

    private TileData[][] map;
    private TileData[][] snapMap;
    private T[] agents;
    private MonstersCaveGui gui;
    private boolean[] agentsFinished;
    private static Point[] initialAgentsPosBase = {
        new Point(1, 0),
        new Point(1, 1),
        new Point(0, 0),
        new Point(0, 1),};

    private Point[] initialAgentsPos;

    public Environment() {
        this.map = null;
        this.agents = null;
        this.initialAgentsPos = null;
    }

    @SuppressWarnings("unchecked")
    public Environment(int n, int nAgents, MonstersCaveGui gui) {
        this.map = new TileData[n][n];
        this.gui = gui;

        setNumberOfAgents(nAgents);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = new TileData();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Environment(TileData[][] map, int nAgents, MonstersCaveGui gui) {
        this.gui = gui;
        this.map = map;

        setNumberOfAgents(nAgents);
    }

    public void snapMap() {
        this.snapMap = new TileData[this.map.length][this.map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                this.snapMap[i][j] = this.map[i][j].getCopy();
            }
        }
    }

    public void restoreMapFromSnap() {
        if (this.snapMap != null) {
            this.map = this.snapMap;
        }
    }

    public TileData[][] getSnap() {
        if (this.snapMap == null) {
            snapMap();
        }
        return this.snapMap;
    }

    public void setNumberOfAgents(int nAgents) {

        initialAgentsPos = new Point[nAgents];
        for (int i = 0; i < initialAgentsPos.length; i++) {
            initialAgentsPos[i] = this.scale(initialAgentsPosBase[i]);
        }

        this.agents = (T[]) new Object[nAgents];
        this.agentsFinished = new boolean[nAgents];
    }

    public boolean isObstacle(int i, int j) {
        return true; //this.map[i][j].is();
    }

    public TileData getIsObstacleReference(int i, int j) {
        return this.map[i][j];
    }

    public void setObstacle(int i, int j, boolean value) {
        //this.map[i][j].setValue(value);
    }

    public void toggleObstacle(int i, int j) {
        //this.map[i][j].toggle();
    }

    public void setAgents(T[] agents) {
        this.agents = agents;
    }

    public T[] getAgents() {
        return this.agents;
    }

    public T getAgent(int i) {
        return this.agents[i];
    }

    public int getSize() {
        return this.map.length;
    }

    public Point[] getInitialPos() {
        return initialAgentsPos;
    }

    public void setInitialPos(Point[] initialPos) {
        this.initialAgentsPos = initialPos;
    }

    @SuppressWarnings("unchecked")
    public void runIteration() {
        for (int i = 0; i < agents.length; i++) {
            Explorer agent = (Explorer) agents[i];
            if (!agentsFinished[i]) {
                agent.processPerceptions(getPerceptions(agent, initialAgentsPos[i]));
                agent.updateFacts(agent.getDisplacement());
                agent.inferBC(agent.getDisplacement());
                agent.checkBC().execute(agent);
            }
        }
    }

    private boolean[] getPerceptions(Explorer agent, Point initialPos) {
        Point explorerDisplacement = agent.getDisplacement();

        TileData tile = null;
        try {
            tile = map[initialPos.x + explorerDisplacement.x][initialPos.y + explorerDisplacement.y];
        } catch (Exception e) {
            tile = new TileData(0, 0, false, true, false, false);
        }

        // hedor, breeze, treasure, obstacle
        return new boolean[]{tile.hasHedor(), tile.hasBreeze(), tile.hasTreasure(), tile.hasObstacle(), this.gui.thereAreTreasures()};
    }

    public TileData[][] getMap() {
        return this.map;
    }

    public int getNAgents() {
        return this.agents.length;
    }

    private Point scale(Point originalPoint) {
        return new Point(
                originalPoint.x * (map.length - 1),
                originalPoint.y * (map.length - 1)
        );
    }

    public void getTreasure(int id) {
        Point displacement = ((Explorer) this.agents[id]).getDisplacement();
        Point basePos = this.initialAgentsPos[id];
        Point treasurePos = new Point(basePos.x + displacement.x, basePos.y + displacement.y);
        this.map[treasurePos.x][treasurePos.y].setHasTreasure(false);
        this.gui.takeTreasure(treasurePos);
    }

    public void shootMonster(int id, Point direction) {
        Point displacement = ((Explorer) this.agents[id]).getDisplacement();
        Point basePos = this.initialAgentsPos[id];
        Point monsterPos = new Point(basePos.x + displacement.x + direction.x, basePos.y + displacement.y + direction.y);
        this.map[monsterPos.x][monsterPos.y].setHasMonster(false);

        for (int[] p : CaveEditor.aroundTiles) {
            try {
                this.map[monsterPos.x + p[0]][monsterPos.y + p[1]].removeHedor();
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        }

        this.gui.killMonster(monsterPos);
    }

    public void putBridge(int id, Point direction) {
        Point displacement = ((Explorer) this.agents[id]).getDisplacement();
        Point basePos = this.initialAgentsPos[id];
        Point holePos = new Point(basePos.x + displacement.x + direction.x, basePos.y + displacement.y + direction.y);
        this.map[holePos.x][holePos.y].setHasHole(false);

        for (int[] p : CaveEditor.aroundTiles) {
            try {
                this.map[holePos.x + p[0]][holePos.y + p[1]].removeBreeze();
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        }

        this.gui.putBridge(holePos);
    }
    
    public void resetAgentsFinished(){
        Arrays.fill(agentsFinished, false);
    }

    public void finished(int id) {
        agentsFinished[id] = true;
        
        for(boolean finished : agentsFinished){
            
            if(!finished){
                return;
            }
        }
        
        this.gui.finishRound();
        
    }
}
