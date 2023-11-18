package productionrules;

import agent.ExplorerMap;
import agent.Labels;
import environment.TileInfo;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BC<T> {

    private LinkedList<Rule<T>> contentRules;
    private ExplorerMap facts;
    private int minTimesVisited;
    private int minIndex;

    public BC() {
        this.contentRules = new LinkedList<>();
        this.facts = new ExplorerMap();
    }

    public BC(List<Rule<T>> contentRules) {
        this.contentRules = new LinkedList<>(contentRules);
        this.facts = new ExplorerMap();
    }

    public void addProdRule(Rule<T> rule) {
        contentRules.add(rule);
    }

    public void updateFacts(Point position, Characteristic[] info) {
        facts.putTile(position, info);
    }

    public void visit(Point position) {
        facts.getTile(position).visit();
    }

    public T getAction() {
        Iterator<Rule<T>> it = contentRules.iterator();

        while (it.hasNext()) {
            Rule<T> rule = it.next();
            if (rule.eval()) {
                return rule.getAction();
            }

        }

        return null;
    }

    public BC<T> infer(Point p, Characteristic[] characteristics) {
        minTimesVisited = Integer.MAX_VALUE;
        minIndex = -1;
        TileInfo[] adjacentTiles = this.getTiles(this.getAdjacentPoints(p));
        for (int i = 0; i < adjacentTiles.length; i++) {
            this.infer(facts.getTile(p), i, adjacentTiles, characteristics);
            characteristics[Labels.PRIO_NORTH.ordinal() + i].setValue(
                    false);
        }

        if (minIndex < 0) {
            return this;
        }

        characteristics[Labels.PRIO_NORTH.ordinal() + minIndex].setValue(
                true);

        return this;
    }

    private void infer(TileInfo center, int currentIndex, TileInfo[] adjacents, Characteristic[] characteristics) {

        TileInfo adj = adjacents[currentIndex];

        // Reglas del juego
        if (center.hasObstacle() && !(adj.notHasObstacle() && adj.getTimesVisited() > 0)) {
            adj.setObstacle();
        }

        if (adj.hasObstacle()) {
            adj.setNotHole();
            adj.setNotMonster();
        }

        if (center.hasHedor() && !(adj.notHasMonster() || adj.hasMonster())) {
            adj.setMaybeMonster();
        }

        if (center.notHasHedor()) {
            adj.setNotMonster();
        }

        if (center.hasBreeze() && !(adj.notHasHole() || adj.hasHole())) {
            adj.setMaybeHole();
        }

        if (center.notHasBreeze()) {
            adj.setNotHole();
        }

        // Reglas de actualizacion o inferencia
        // Remove maybes
        if (adj.notHasHole() || adj.hasHole()) {
            adj.removeMaybeHole();
        }

        if (adj.notHasMonster() || adj.hasMonster()) {
            adj.removeMaybeMonster();
        }

        // Monster
        boolean thereIsMonster = center.hasHedor();
        for (int i = 0; i < adjacents.length; i++) {
            if (i == currentIndex) {
                continue;
            }

            TileInfo tile = adjacents[i];

            thereIsMonster = thereIsMonster && tile.notHasMonster();

        }

        if (thereIsMonster) {
            adj.setMonster();
            adj.setNotHole();
        }

        // Hole
        boolean thereIsHole = center.hasBreeze();
        for (int i = 0; i < adjacents.length; i++) {
            if (i == currentIndex) {
                continue;
            }

            TileInfo tile = adjacents[i];

            thereIsHole = thereIsHole && tile.notHasHole();

        }

        if (thereIsHole) {
            adj.setHole();
            adj.setNotMonster();
        }

        if (adj.isOk() && !adj.hasObstacle()) {
            int timesVisited = adj.getTimesVisited();
            if (timesVisited < minTimesVisited) {
                minTimesVisited = timesVisited;
                minIndex = currentIndex;
            }
        }

        // TIENE MONSTRUO EN X -> DISPARAR HACIA X
        characteristics[Labels.SHOOT_NORTH.ordinal() + currentIndex].setValue(adj.hasMonster());

        // TIENE AGUJERO EN X -> PUENTE EN X
        characteristics[Labels.BRIDGE_NORTH.ordinal() + currentIndex].setValue(adj.hasHole());

    }

    public List<Rule<T>> getContentRules() {
        return contentRules;
    }

    public String toStringEvaluated() {
        Iterator<Rule<T>> it = contentRules.iterator();
        String str = "";

        while (it.hasNext()) {
            Rule<T> rule = it.next();
            str += rule.toString() + ": " + rule.eval() + "\n";
        }
        return str;

    }

    @Override
    public String toString() {
        Iterator<Rule<T>> it = contentRules.iterator();
        String str = "";

        while (it.hasNext()) {
            str += it.next().toString() + "\n";
        }
        return str;
    }

    private Point[] getAdjacentPoints(Point p) {
        return new Point[] {
                new Point(p.x - 1, p.y), // Arriba
                new Point(p.x + 1, p.y), // Abajo
                new Point(p.x, p.y + 1), // Derecha
                new Point(p.x, p.y - 1) // Izquierda
        };
    }

    private TileInfo[] getTiles(Point[] points) {
        TileInfo[] tiles = new TileInfo[points.length];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = facts.getTile(points[i]);
        }
        return tiles;
    }

    public ExplorerMap getMap() {
        return this.facts;
    }

}
