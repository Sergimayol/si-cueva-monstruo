
package agent;

import environment.TileInfo;
import java.awt.Point;
import java.util.HashMap;

import agent.labels.Labels;
import agent.rules.Characteristic;

public class ExplorerMap extends HashMap<String, TileInfo> {

    public ExplorerMap() {
        super();
    }

    public TileInfo putTile(Point position, Characteristic[] info) {
        TileInfo prevInfo = this.getTile(position);

        if (info != null) {

            // Si es obstaculo se pone
            if (evalCharacteristic(Labels.OBSTACLE, info)) {
                prevInfo.setObstacle();
            }

            // Si no es obstaculo se pone
            if (evalCharacteristic(Labels.NOT_OBSTACLE, info)) {
                prevInfo.setNotObstacle();
            }

            // Si habia brisa y antes no habias detectado que no hubiera pones que hay brisa
            if (evalCharacteristic(Labels.BREEZE, info)
                    && !prevInfo.notHasBreeze()) {
                prevInfo.setBreeze();
            }

            // Si no habia brisa y antes no habias detectado que hubiera pones que no hay
            // brisa
            if (evalCharacteristic(Labels.NOT_BREEZE, info)) {
                prevInfo.setNotBreeze();
            }

            // Si habia hedor y antes no habias detectado que no hubiera pones que hay hedor
            if (evalCharacteristic(Labels.HEDOR, info)
                    && !prevInfo.notHasHedor()) {
                prevInfo.setHedor();
            }

            // Si no habia hedor y antes no habias detectado que hubiera pones que no hay
            // hedor
            if (evalCharacteristic(Labels.NOT_HEDOR, info)) {
                prevInfo.setNotHedor();
            }

            prevInfo.setNotMonster();
            prevInfo.setNotHole();
        }

        return prevInfo;

    }

    public TileInfo getTile(Point position) {

        TileInfo info = this.get(this.pointToString(position));

        if (info == null) {
            info = new TileInfo();
            this.put(this.pointToString(position), info);
        }

        return info;
    }

    public TileInfo getTileNoAdd(Point position) {
        return this.get(this.pointToString(position));
    }

    private boolean evalCharacteristic(Labels label, Characteristic[] info) {
        return info[label.ordinal()].getValue();
    }

    private String pointToString(Point p) {
        return p.x + "," + p.y;
    }

}
