
package environment;

import java.io.Serializable;

public class TileData implements Serializable {

    // Signals
    private int hedorCounter;
    private int breezeCounter;
    private boolean hasTreasure;
    private boolean hasObstacle;

    // Entities
    private boolean hasMonster;
    private boolean hasHole;

    public TileData() {
        this.hedorCounter = 0;
        this.breezeCounter = 0;
        this.hasTreasure = false;
        this.hasObstacle = false;
        this.hasMonster = false;
        this.hasHole = false;
    }

    public TileData(int hedorCounter, int breezeCounter, boolean hasTreasure,
            boolean hasObstacle, boolean hasMonster, boolean hasHole) {
        this.hedorCounter = hedorCounter;
        this.breezeCounter = breezeCounter;
        this.hasTreasure = hasTreasure;
        this.hasObstacle = hasObstacle;
        this.hasMonster = hasMonster;
        this.hasHole = hasHole;
    }

    public boolean hasHedor() {
        return this.hedorCounter > 0;
    }

    public void addHedor() {
        this.hedorCounter++;
    }

    public void removeHedor() {
        this.hedorCounter--;
    }

    public boolean hasBreeze() {
        return this.breezeCounter > 0;
    }

    public void addBreeze() {
        this.breezeCounter++;
    }

    public void removeBreeze() {
        this.breezeCounter--;
    }

    public boolean hasTreasure() {
        return hasTreasure;
    }

    public void setHasTreasure(boolean hasTreasure) {
        this.hasTreasure = hasTreasure;
    }

    public boolean hasObstacle() {
        return hasObstacle;
    }

    public void setHasObstacle(boolean hasObstacle) {
        this.hasObstacle = hasObstacle;
    }

    public boolean hasMonster() {
        return hasMonster;
    }

    public void setHasMonster(boolean hasMonster) {
        this.hasMonster = hasMonster;
    }

    public boolean hasHole() {
        return hasHole;
    }

    public void setHasHole(boolean hasHole) {
        this.hasHole = hasHole;
    }

    public TileData getCopy() {
        return new TileData(hedorCounter, breezeCounter, hasTreasure,
                hasObstacle, hasMonster, hasHole);
    }

    @Override
    public String toString() {
        return ("=== TileData ===\n" +
                "hedorCounter: " +
                hedorCounter +
                "\n" +
                "breezeCounter: " +
                breezeCounter +
                "\n" +
                "hasMonster: " +
                hasMonster +
                "\n" +
                "hasHole: " +
                hasHole +
                "\n" +
                "hasTreasure: " +
                hasTreasure +
                "\n" +
                "hasObstacle: " +
                hasObstacle +
                "\n");
    }
}
