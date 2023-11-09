package agent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import agent.labels.Action;
import agent.labels.CharacteristicLabels;
import agent.rules.BC;
import agent.rules.Characteristic;
import env.Environment;
import env.TileInfo;
import utils.FileLogger;
import utils.RichPoint;

public class Explorer extends BaseAgent<Executable> {

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
        this.initCharacteristics();
        this.move(0, 0);
        this.initBC();
    }

    public Explorer(int id, Environment<Explorer> env) {
        this(id, env, true, true);
    }

    private void initCharacteristics() {
        characteristics = new Characteristic[CharacteristicLabels.values().length];
        for (int i = 0; i < characteristics.length; i++) {
            characteristics[i] = new Characteristic(CharacteristicLabels.values()[i].name());
        }
        FileLogger.info("[ROBOT] Characteristics initialized, there are " + characteristics.length
                + " characteristics");
    }

    private void initBC() {
        FileLogger.info("[ROBOT] Initializing BC...");
        // RETURN HOME
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_TREASURES_REMAINING },
                Action.RETURN_HOME);

        // TAKE TREASURE
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.TREASURE },
                Action.TAKE_TREASURE);

        // SHOOT TO THE MONSTER
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_SHOOT,
                CharacteristicLabels.SHOOT_NORTH },
                Action.SHOOT_NORTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_SHOOT,
                CharacteristicLabels.SHOOT_SOUTH },
                Action.SHOOT_SOUTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_SHOOT,
                CharacteristicLabels.SHOOT_EAST },
                Action.SHOOT_EAST);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_SHOOT,
                CharacteristicLabels.SHOOT_WEST },
                Action.SHOOT_WEST);

        // PUT BRIDGE
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_PUT_BRIDGE,
                CharacteristicLabels.BRIDGE_NORTH },
                Action.BRIDGE_NORTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_PUT_BRIDGE,
                CharacteristicLabels.BRIDGE_SOUTH },
                Action.BRIDGE_SOUTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_PUT_BRIDGE,
                CharacteristicLabels.BRIDGE_EAST },
                Action.BRIDGE_EAST);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.CAN_PUT_BRIDGE,
                CharacteristicLabels.BRIDGE_WEST },
                Action.BRIDGE_WEST);

        // MOVE WITHOUT PRIORITY
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.PRIO_NORTH }, Action.MOVE_NORTH);
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.PRIO_SOUTH }, Action.MOVE_SOUTH);
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.PRIO_EAST }, Action.MOVE_EAST);
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.PRIO_WEST }, Action.MOVE_WEST);

        // Default action
        this.addRule(new CharacteristicLabels[] {}, Action.NOT_MOVE);
        FileLogger.info("[ROBOT] BC initialized");
    }

    @Override
    public void processInputSensors(boolean[] sensors) {
        FileLogger.info("[ROBOT] Processing input sensors... (" + Arrays.toString(sensors) + ")");
        for (int i = 0; i < sensors.length; i++) {
            characteristics[i * 2].setValue(sensors[i]);
            characteristics[(i * 2) + 1].setValue(!sensors[i]);
        }

        characteristics[CharacteristicLabels.CAN_SHOOT.ordinal()].setValue(this.canShoot);
        characteristics[CharacteristicLabels.CAN_PUT_BRIDGE.ordinal()].setValue(this.canPutBridge);
        FileLogger.info("[ROBOT] Input sensors processed, new characteristics are: "
                + Arrays.toString(characteristics));
    }

    public void move(int x, int y) {
        this.displacement.x += x;
        this.displacement.y += y;
        this.bc.visit(displacement);
    }

    public void setDefaultPosition() {
        this.displacement.x = -1;
        this.displacement.y = -1;
    }

    public boolean isDefaultPosition() {
        return this.displacement.x == -1 && this.displacement.y == -1;
    }

    public int getId() {
        return this.id;
    }

    public Point getDisplacement() {
        return this.displacement;
    }

    public void finished() {
        this.env.finished(id);
    }

    public void takeTreasure() {
        this.treasures++;
        this.env.getTreasure(this.id);
    }

    public void shootMonster(int x, int y) {
        this.env.shootMonster(this.id, new Point(x, y));
    }

    public void putBridge(int x, int y) {
        this.env.putBridge(this.id, new Point(x, y));
    }

    public List<RichPoint> getMovemementsToHome() {
        return this.movemementsToHome;
    }

    public void setMovemementsToHome(List<RichPoint> points) {
        this.movemementsToHome = new ArrayList<>(points);
    }

    public ExplorerMem getMap() {
        return this.bc.getMap();
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

    public List<RichPoint> calculateActionsToHome(RichPoint origen, RichPoint desti) {
        ArrayList<RichPoint> path = new ArrayList<>();

        ArrayList<RichPoint> tancats = new ArrayList<>();
        Queue<RichPoint> oberts = new PriorityQueue<>();
        RichPoint actual = null;

        ArrayList<RichPoint> successors;
        boolean trobat = false;

        origen.distanceFromOrigin = 0;
        oberts.add(origen);

        while (!trobat && !oberts.isEmpty()) {

            actual = oberts.poll();

            if (actual.equals(desti)) {
                trobat = true;
            } else {
                successors = getAdjacentPoints(actual);
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

                tancats.add(new RichPoint(actual.x, actual.y, actual.previous));
            }

        }

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
