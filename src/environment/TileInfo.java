/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package environment;

/**
 *
 * @author ccf20
 */
public class TileInfo {

    // Information
    private int timesVisited = 0;

    // Signals
    private boolean hasHedor;
    private boolean notHasHedor;
    private boolean hasBreeze;
    private boolean notHasBreeze;
//    private boolean hasTreasure;
//    private boolean notHasTreasure;
    private boolean hasObstacle;
    private boolean notHasObstacle;

    // Entities
    private boolean hasMonster;
    private boolean notHasMonster;
    private boolean maybeHasMonster;
    private boolean hasHole;
    private boolean notHasHole;
    private boolean maybeHasHole;

    public TileInfo() {

    }

    public TileInfo(boolean hasHedor, boolean notHasHedor, boolean hasBreeze,
            boolean notHasBreeze, /*boolean hasTreasure, boolean notHasTreasure,*/
            boolean hasObstacle, boolean notHasObstacle, boolean hasMonster,
            boolean notHasMonster, boolean maybeHasMonster, boolean hasHole,
            boolean notHasHole, boolean maybeHasHole) {
        this.hasHedor = hasHedor;
        this.notHasHedor = notHasHedor;
        this.hasBreeze = hasBreeze;
        this.notHasBreeze = notHasBreeze;
//        this.hasTreasure = hasTreasure;
//        this.notHasTreasure = notHasTreasure;
        this.hasObstacle = hasObstacle;
        this.notHasObstacle = notHasObstacle;
        this.hasMonster = hasMonster;
        this.notHasMonster = notHasMonster;
        this.maybeHasMonster = maybeHasMonster;
        this.hasHole = hasHole;
        this.notHasHole = notHasHole;
        this.maybeHasHole = maybeHasHole;
    }

    public int getTimesVisited() {
        return timesVisited;
    }

    public void setTimesVisited(int timesVisited) {
        this.timesVisited = timesVisited;
    }

    public void visit() {
        timesVisited++;
    }

    public boolean isOk() {
        return this.notHasMonster && this.notHasHole;
    }

    public boolean hasHedor() {
        return hasHedor;
    }

    public void setHedor() {
        this.notHasHedor = false;
        this.hasHedor = true;
    }

    public boolean notHasHedor() {
        return notHasHedor;
    }

    public void setNotHedor() {
        this.hasHedor = false;
        this.notHasHedor = true;
    }

    public boolean hasBreeze() {
        return hasBreeze;
    }

    public void setBreeze() {
        this.notHasBreeze = false;
        this.hasBreeze = true;
    }

    public boolean notHasBreeze() {
        return notHasBreeze;
    }

    public void setNotBreeze() {
        this.hasBreeze = false;
        this.notHasBreeze = true;
    }

    public boolean hasMonster() {
        return hasMonster;
    }

    public void setMonster() {
        this.notHasMonster = false;
        this.hasMonster = true;
    }

    public boolean notHasMonster() {
        return notHasMonster;
    }

    public void setNotMonster() {
        this.hasMonster = false;
        this.notHasMonster = true;
    }

    public boolean maybeHasMonster() {
        return maybeHasMonster;
    }

    public void setMaybeMonster() {
        this.maybeHasMonster = true;
    }

    public boolean hasHole() {
        return hasHole;
    }

    public void setHole() {
        this.notHasHole = false;
        this.hasHole = true;
    }

    public boolean notHasHole() {
        return notHasHole;
    }

    public void setNotHole() {
        this.hasHole = false;
        this.notHasHole = true;
    }

    public boolean maybeHasHole() {
        return maybeHasHole;
    }

    public void setMaybeHole() {
        this.maybeHasHole = true;
    }

//    public boolean hasTreasure() {
//        return hasTreasure;
//    }
//
//    public void setTreasure() {
//        this.hasTreasure = true;
//    }
//
//    public boolean notHasTreasure() {
//        return notHasTreasure;
//    }
//
//    public void setNotTreasure() {
//        this.notHasTreasure = true;
//    }

    public boolean hasObstacle() {
        return hasObstacle;
    }

    public void setObstacle() {
        this.hasObstacle = true;
    }

    public boolean notHasObstacle() {
        return notHasObstacle;
    }

    public void setNotObstacle() {
        this.notHasObstacle = true;
    }

    public void removeMaybeMonster() {
        this.maybeHasMonster = false;
    }

    public void removeMaybeHole() {
        this.maybeHasHole = false;
    }

    @Override
    public String toString() {
        return ("=== TileInfo ===\n"
                + "timesVisited: "
                + timesVisited
                + "\n"
                + "hedorCounter: "
                + hasHedor
                + "\n"
                + "breezeCounter: "
                + hasBreeze
                + "\n"
                + "hasMonster: "
                + hasMonster
                + "\n"
                + "hasHole: "
                + hasHole
                + "\n"
                + "hasObstacle: "
                + hasObstacle
                + "\n");
    }
}
