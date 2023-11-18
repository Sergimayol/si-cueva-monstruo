package agent;

import environment.Environment;
import environment.TileInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;
import productionrules.BC;
import productionrules.Characteristic;
import utils.RichPoint;

public class Explorer extends GridAgent<Executable> {

    public int getId() {
        return this.id;
    }

    public enum Action implements Executable {
        MOVE_NORTH {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).move(-1, 0);

            }
        },
        MOVE_SOUTH {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).move(1, 0);
            }
        },
        MOVE_EAST {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).move(0, 1);
            }
        },
        MOVE_WEST {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).move(0, -1);
            }
        },
        SHOOT_NORTH {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());

                ((Explorer) explorer).shootMonster(-1, 0);
            }
        },
        SHOOT_SOUTH {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).shootMonster(1, 0);
            }
        },
        SHOOT_EAST {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).shootMonster(0, 1);
            }
        },
        SHOOT_WEST {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).shootMonster(0, -1);
            }
        },
        BRIDGE_NORTH {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());

                ((Explorer) explorer).putBridge(-1, 0);
            }
        },
        BRIDGE_SOUTH {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).putBridge(1, 0);
            }
        },
        BRIDGE_EAST {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).putBridge(0, 1);
            }
        },
        BRIDGE_WEST {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).putBridge(0, -1);
            }
        },
        TAKE_TREASURE {
            @Override
            public void execute(Object explorer) {
                //                System.out.println("[Robot.java] Action: " + this.toString());
                ((Explorer) explorer).takeTreasure();
            }
        },
        RETURN_HOME {
            @Override
            public void execute(Object explorer) {
                Explorer currExplorer = ((Explorer) explorer);

                if (currExplorer.getDisplacement().x == 0 && currExplorer.getDisplacement().y == 0) {
                    currExplorer.finished();
                    return;
                }

                if (currExplorer.getMovemementsToHome() == null) {
                    currExplorer.setMovemementsToHome(
                            currExplorer.calculateActionsToHome(
                                    new RichPoint(currExplorer.getDisplacement()),
                                    new RichPoint(0, 0)
                            )
                    );

                }

                if (currExplorer.getMovemementsToHome().size() > 0) {

                    RichPoint movement = currExplorer.getMovemementsToHome().remove(0);
                    currExplorer.move(movement.x, movement.y);

                }

            }
        },
        NOT_MOVE {
            @Override
            public void execute(Object explorer) {
            }
        };

        public void execute(Explorer explorer) {
            System.out.println("NOT IMPLEMENTED");
        }
    }

    // Characteristics labels
    // PRIO_NORTH,
    // PRIO_SOUTH,
    // PRIO_EAST,
    // PRIO_WEST,
    // ORDER: hedor, breeze, treasure, obstacle
    public enum Labels {
        HEDOR,
        NOT_HEDOR,
        BREEZE,
        NOT_BREEZE,
        TREASURE,
        NOT_TREASURE,
        OBSTACLE,
        NOT_OBSTACLE,
        TREASURES_REMAINING,
        NOT_TREASURES_REMAINING,
        CAN_SHOOT,
        CAN_PUT_BRIDGE,
        PRIO_NORTH,
        PRIO_SOUTH,
        PRIO_EAST,
        PRIO_WEST,
        SHOOT_NORTH,
        SHOOT_SOUTH,
        SHOOT_EAST,
        SHOOT_WEST,
        BRIDGE_NORTH,
        BRIDGE_SOUTH,
        BRIDGE_EAST,
        BRIDGE_WEST
    }

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
    
    

    public ArrayList<RichPoint> getMovemementsToHome() {
        return movemementsToHome;
    }

    public void setMovemementsToHome(ArrayList<RichPoint> points) {
        this.movemementsToHome = points;
    }

    private void initBC() {

        // RETURN HOME
        this.addProdRule(new int[]{Labels.NOT_TREASURES_REMAINING.ordinal()}, Explorer.Action.RETURN_HOME);

        // TAKE TREASURE
        this.addProdRule(new int[]{
            Labels.TREASURE.ordinal()
        },
                Explorer.Action.TAKE_TREASURE);

        // SHOOT TO THE MONSTER
        this.addProdRule(new int[]{Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_NORTH.ordinal()}, Explorer.Action.SHOOT_NORTH);
        this.addProdRule(new int[]{Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_SOUTH.ordinal()}, Explorer.Action.SHOOT_SOUTH);
        this.addProdRule(new int[]{Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_EAST.ordinal()}, Explorer.Action.SHOOT_EAST);
        this.addProdRule(new int[]{Labels.CAN_SHOOT.ordinal(), Labels.SHOOT_WEST.ordinal()}, Explorer.Action.SHOOT_WEST);

        // PUT BRIDGE
        this.addProdRule(new int[]{Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_NORTH.ordinal()}, Explorer.Action.BRIDGE_NORTH);
        this.addProdRule(new int[]{Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_SOUTH.ordinal()}, Explorer.Action.BRIDGE_SOUTH);
        this.addProdRule(new int[]{Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_EAST.ordinal()}, Explorer.Action.BRIDGE_EAST);
        this.addProdRule(new int[]{Labels.CAN_PUT_BRIDGE.ordinal(), Labels.BRIDGE_WEST.ordinal()}, Explorer.Action.BRIDGE_WEST);

        // MOVE WITHOUT PRIORITY
        this.addProdRule(new int[]{Labels.PRIO_NORTH.ordinal()}, Explorer.Action.MOVE_NORTH);
        this.addProdRule(new int[]{Labels.PRIO_SOUTH.ordinal()}, Explorer.Action.MOVE_SOUTH);
        this.addProdRule(new int[]{Labels.PRIO_EAST.ordinal()}, Explorer.Action.MOVE_EAST);
        this.addProdRule(new int[]{Labels.PRIO_WEST.ordinal()}, Explorer.Action.MOVE_WEST);

        // DEFAULT ACTION (CAN'T MOVE)
        this.addProdRule(new int[]{}, Explorer.Action.NOT_MOVE);

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
        points.add(new RichPoint(p.x - 1, p.y, p)); //Arriba
        points.add(new RichPoint(p.x + 1, p.y, p)); //Abajo
        points.add(new RichPoint(p.x, p.y + 1, p)); //Derecha
        points.add(new RichPoint(p.x, p.y - 1, p)); //Izquierda

        TileInfo[] tiles = getTiles(points);
        for (int i = tiles.length - 1; i >= 0; i--) {
            TileInfo tile = tiles[i];
            if (tile != null && !tile.hasObstacle() && tile.isOk()) {
                continue;
            }
            System.out.println(tile);
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
        punt.distanceToEnd = Math.abs(punt.x - desti.x) + Math.abs(punt.y - desti.y);
    }

    public ArrayList<RichPoint> calculateActionsToHome(RichPoint origen, RichPoint desti) {   // Tipus pot ser MANHATTAN o EUCLIDIA
        ArrayList<RichPoint> path = new ArrayList<>();

        // Implementa l'algoritme aquí
        //Cream oberts, tancats i les variables necessaries per l'algoritme
        ArrayList tancats = new ArrayList<RichPoint>();
        Queue oberts = new PriorityQueue<RichPoint>();//Oberts es un heap perque es cerca heuristica
        RichPoint actual = null;

        ArrayList<RichPoint> successors;
        boolean trobat = false;

        // Afegim el node inicial a la llista de tancats
        origen.distanceFromOrigin = 0; //El primer node té distancia zero a si mateix
        oberts.add(origen);

        // Mentre no trobem el cami i hagi nodes per visitar iteram
        while (!trobat && !oberts.isEmpty()) {

            // Agafam el primer
            actual = (RichPoint) oberts.poll();

            // Si hem trobat el node final aturem la cerca
            if (actual.equals(desti)) {
                trobat = true;
            } else {
                // Si no
                // Agafam els successors valids
                successors = getAdjacentPoints(actual);
                //Afegim cada successor que no estigui dins oberts ni tancats a oberts
                for (RichPoint successor : successors) {

                    successor.distanceFromOrigin = actual.distanceFromOrigin + 1;

                    if (isInClosed(tancats, successor)) {
                        for (int i = 0; i < tancats.size(); i++) {
                            RichPoint punt = (RichPoint) tancats.get(i);
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
                        for (Object p : oberts) {
                            RichPoint punt = (RichPoint) p;
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
            //System.out.println(subtractPoints(previ, actual));
            previ = actual;
            actual = actual.previous;
        }
        path.add(subtractPoints(previ, origen));
        //System.out.println(subtractPoints(previ, origen));

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
