package agent;

import environment.Environment;
import environment.TileInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import productionrules.BC;
import productionrules.Characteristic;
import utils.RichPoint;

public class Explorer extends GridAgent<Executable<Explorer>> {

    private Point displacement;
    private boolean canShoot;
    private boolean canPutBridge;

    private int treasures = 0;
    private final int id;
    private final Environment<Explorer> env;
    private ArrayList<RichPoint> movemementsToHome;

    public Explorer(int id, Environment<Explorer> env, boolean canShoot, boolean canPutBridge) {
        this.bc = new BC<>();
        this.displacement = new Point(0, 0);
        this.env = env;
        this.id = id;
        this.movemementsToHome = null;
        this.canPutBridge = canPutBridge;
        this.canShoot = canShoot;

        characteristics = new Characteristic[Labels.values().length];
        for (int i = 0; i < characteristics.length; i++) {
            characteristics[i] = new Characteristic(Labels.values()[i].name());
        }

        this.move(0, 0);

        this.initBC();
    }

    public void setCanShoot(boolean canShoot) {
        this.canShoot = canShoot;
    }

    public void setCanPutBridge(boolean canPutBridge) {
        this.canPutBridge = canPutBridge;
    }

    public List<RichPoint> getMovemementsToHome() {
        return movemementsToHome;
    }

    public void setMovemementsToHome(List<RichPoint> points) {
        this.movemementsToHome = new ArrayList<>(points);
    }

    public int getId() {
        return this.id;
    }

    private void initBC() {

        // RETURN HOME
        this.addProdRule(new int[] { Labels.NOT_TREASURES_REMAINING.ordinal() }, Action.RETURN_HOME);

        // TAKE TREASURE
        this.addProdRule(new int[] { Labels.TREASURE.ordinal() }, Action.TAKE_TREASURE);

        // SHOOT TO THE MONSTER
        this.addProdRule(new int[] { Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_NORTH.ordinal() },
                Action.SHOOT_NORTH);
        this.addProdRule(new int[] { Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_SOUTH.ordinal() },
                Action.SHOOT_SOUTH);
        this.addProdRule(new int[] { Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_EAST.ordinal() },
                Action.SHOOT_EAST);
        this.addProdRule(new int[] { Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_WEST.ordinal() },
                Action.SHOOT_WEST);

        // PUT BRIDGE
        this.addProdRule(new int[] { Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_NORTH.ordinal() },
                Action.BRIDGE_NORTH);
        this.addProdRule(new int[] { Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_SOUTH.ordinal() },
                Action.BRIDGE_SOUTH);
        this.addProdRule(new int[] { Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_EAST.ordinal() },
                Action.BRIDGE_EAST);
        this.addProdRule(new int[] { Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_WEST.ordinal() },
                Action.BRIDGE_WEST);

        // MOVE WITHOUT PRIORITY
        this.addProdRule(new int[] { Labels.PRIO_NORTH.ordinal() }, Action.MOVE_NORTH);
        this.addProdRule(new int[] { Labels.PRIO_SOUTH.ordinal() }, Action.MOVE_SOUTH);
        this.addProdRule(new int[] { Labels.PRIO_EAST.ordinal() }, Action.MOVE_EAST);
        this.addProdRule(new int[] { Labels.PRIO_WEST.ordinal() }, Action.MOVE_WEST);

        // DEFAULT ACTION (CAN'T MOVE)
        this.addProdRule(new int[] {}, Action.NOT_MOVE);

    }

    public Point getDisplacement() {
        return this.displacement;
    }

    public void move(int x, int y) {
        this.displacement.x += x;
        this.displacement.y += y;

        this.bc.visit(displacement);

    }

    public void finished() {
        this.env.finished(id);
    }

    public void takeTreasure() {
        treasures++;
        this.env.getTreasure(this.id);
    }

    public void shootMonster(int x, int y) {
        this.env.shootMonster(this.id, new Point(x, y));
    }

    public void putBridge(int x, int y) {
        this.env.putBridge(this.id, new Point(x, y));
    }

    public ExplorerMap getMap() {
        return this.bc.getMap();
    }

    @Override
    public void processPerceptions(boolean[] perceptions) {
        // ORDER: hedor, breeze, treasure, obstacle

        for (int i = 0; i < perceptions.length; i++) {
            characteristics[i * 2].setValue(perceptions[i]);
            characteristics[(i * 2) + 1].setValue(!perceptions[i]);
        }

        characteristics[Labels.CAN_SHOOT.ordinal()].setValue(this.canShoot);
        characteristics[Labels.CAN_PUT_BRIDGE.ordinal()].setValue(this.canPutBridge);
    }

    public String getTreasuresTaken() {
        return Integer.toString(this.treasures);
    }

    private ArrayList<RichPoint> getAdjacentPoints(RichPoint p) {
        ArrayList<RichPoint> points = new ArrayList<>();
        points.add(new RichPoint(p.x - 1, p.y, p)); // Arriba
        points.add(new RichPoint(p.x + 1, p.y, p)); // Abajo
        points.add(new RichPoint(p.x, p.y + 1, p)); // Derecha
        points.add(new RichPoint(p.x, p.y - 1, p)); // Izquierda

        TileInfo[] tiles = getTiles(points);
        for (int i = tiles.length - 1; i >= 0; i--) {
            TileInfo tile = tiles[i];
            if (tile != null && !tile.hasObstacle() && tile.isOk()) {
                continue;
            }
            points.remove(i);

        }

        return points;
    }

    private TileInfo[] getTiles(ArrayList<RichPoint> points) {
        TileInfo[] tiles = new TileInfo[points.size()];
        for (int i = 0; i < tiles.length; i++) {
            RichPoint p = points.get(i);
            tiles[i] = this.getMap().getTileNoAdd(new Point(p.x, p.y));
        }
        return tiles;
    }

    private void calculateManhattan(RichPoint punt, RichPoint desti) {
        punt.distanceToEnd = Math.abs(punt.x - desti.x) + (double) Math.abs(punt.y - desti.y);
    }

    public List<RichPoint> calculateActionsToHome(RichPoint origen, RichPoint desti) { // Tipus pot ser MANHATTAN o
                                                                                       // EUCLIDIA
        ArrayList<RichPoint> path = new ArrayList<>();

        // Implementa l'algoritme aquí
        // Cream oberts, tancats i les variables necessaries per l'algoritme
        ArrayList<RichPoint> tancats = new ArrayList<>();
        Queue<RichPoint> oberts = new PriorityQueue<>();// Oberts es un heap perque es cerca heuristica
        RichPoint actual = null;

        ArrayList<RichPoint> successors;
        boolean trobat = false;

        // Afegim el node inicial a la llista de tancats
        origen.distanceFromOrigin = 0; // El primer node té distancia zero a si mateix
        oberts.add(origen);

        // Mentre no trobem el cami i hagi nodes per visitar iteram
        while (!trobat && !oberts.isEmpty()) {

            // Agafam el primer
            actual = oberts.poll();

            // Si hem trobat el node final aturem la cerca
            if (actual.equals(desti)) {
                trobat = true;
            } else {
                // Si no
                // Agafam els successors valids
                successors = getAdjacentPoints(actual);
                // Afegim cada successor que no estigui dins oberts ni tancats a oberts
                for (RichPoint successor : successors) {

                    successor.distanceFromOrigin = actual.distanceFromOrigin + 1;

                    if (isInClosed(tancats, successor)) {
                        for (int i = 0; i < tancats.size(); i++) {
                            RichPoint punt = tancats.get(i);
                            if (successor == punt) {
                                if (successor.distanceFromOrigin < punt.distanceFromOrigin) {
                                    tancats.remove(i);
                                    punt.previous = actual;
                                    oberts.add(punt);
                                }
                                break;
                            }
                        }
                    } else if (oberts.contains(successor)) {
                        for (RichPoint punt : oberts) {
                            if (successor == punt) {
                                if (successor.distanceFromOrigin < punt.distanceFromOrigin) {
                                    punt.previous = actual;
                                }
                                break;
                            }
                        }
                    } else {
                        calculateManhattan(successor, desti);
                        oberts.add(successor);
                    }

                }

                // Ficam el primer element de la coa d'oberts a la llista de tancats
                tancats.add(new RichPoint(actual.x, actual.y, actual.previous));
            }

        }

        // Crear camí
        RichPoint previ = desti;
        actual = actual.previous;
        while (!actual.equals(origen)) {
            path.add(subtractPoints(previ, actual));
            previ = actual;
            actual = actual.previous;
        }
        path.add(subtractPoints(previ, origen));

        Collections.reverse(path);

        return path;
    }

    private boolean isInClosed(ArrayList<RichPoint> tancats, RichPoint p) {
        for (RichPoint punt : tancats) {
            if (punt.equals(p)) {
                return true;
            }
        }
        return false;
    }

    private RichPoint subtractPoints(RichPoint p1, RichPoint p2) {
        return new RichPoint(p1.x - p2.x, p1.y - p2.y);
    }

}
