package agent;

import java.util.HashMap;

import agent.labels.CharacteristicLabels;
import agent.rules.Characteristic;
import env.TileInfo;

import java.awt.Point;

public class ExplorerMem extends HashMap<String, TileInfo> {

    public ExplorerMem() {
        super();
    }

    public TileInfo putTile(Point position, Characteristic[] info) {
        TileInfo prevInfo = this.getTile(position);

        if (prevInfo == null) {
            return null;
        }

        // Si es obstaculo se pone
        if (evalCharacteristic(CharacteristicLabels.OBSTACLE, info)) {
            prevInfo.setObstacle();
        }

        // Si no es obstaculo se pone
        if (evalCharacteristic(CharacteristicLabels.NOT_OBSTACLE, info)) {
            prevInfo.setNotObstacle();
        }

        // Si habia brisa y antes no habias detectado que no hubiera pones que hay brisa
        if (evalCharacteristic(CharacteristicLabels.BREEZE, info)
                && !prevInfo.notHasBreeze()) {
            prevInfo.setBreeze();
        }

        // Si no habia brisa y antes no habias detectado que hubiera pones que no hay
        // brisa
        if (evalCharacteristic(CharacteristicLabels.NOT_BREEZE, info)) {
            prevInfo.setNotBreeze();
        }

        // Si habia hedor y antes no habias detectado que no hubiera pones que hay hedor
        if (evalCharacteristic(CharacteristicLabels.HEDOR, info)
                && !prevInfo.notHasHedor()) {
            prevInfo.setHedor();
        }

        // Si no habia hedor y antes no habias detectado que hubiera pones que no hay
        // hedor
        if (evalCharacteristic(CharacteristicLabels.NOT_HEDOR, info)) {
            prevInfo.setNotHedor();
        }

        prevInfo.setNotMonster();
        prevInfo.setNotHole();

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

    private boolean evalCharacteristic(CharacteristicLabels label, Characteristic[] info) {
        return info[label.ordinal()].getValue();
    }

    private String pointToString(Point p) {
        return p.x + "," + p.y;
    }

}
