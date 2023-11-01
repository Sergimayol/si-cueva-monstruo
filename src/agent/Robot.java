package agent;

import java.awt.Point;
import java.util.Arrays;

import agent.labels.Action;
import agent.labels.CharacteristicLabels;
import agent.labels.LookingAt;
import agent.rules.BC;
import agent.rules.Characteristic;

import utils.FileLogger;

public class Robot extends BaseAgent<Executable> {

    private LookingAt direction;

    public Robot() {
        this.position = new Point(-1, -1);
        this.direction = LookingAt.NORTH;
        this.bc = new BC<>();
        this.initCharacteristics();
        this.initBC();
    }

    private void initCharacteristics() {
        characteristics = new Characteristic[CharacteristicLabels.values().length];
        for (int i = 0; i < characteristics.length; i++) {
            characteristics[i] = new Characteristic(CharacteristicLabels.values()[i].name());
        }
        FileLogger.info("[ROBOT] Characteristics initialized, there are " + characteristics.length
                + " characteristics");
    }

    public LookingAt getDirection() {
        return direction;
    }

    public void setLooking(LookingAt dir) {
        this.direction = dir;
    }

    public boolean isDefaultPosition() {
        return this.position.x == -1 && this.position.y == -1;
    }

    public void setDefaultPosition() {
        this.position.x = -1;
        this.position.y = -1;
    }

    /**
     * NORTE = x4 and not x1 -> (s8 v s1) and not (s2 v s3)
     * OESTE = x3 and not x4 -> (s6 v s7) and not (s8 v s1)
     * SUR = x2 and not x3 -> (s4 v s5) and not (s6 v s7)
     * ESTE = x1 and not x2 -> (s2 v s3) and not (s4 v s5)
     */
    private void initBC() {
        FileLogger.info("[ROBOT] Initializing BC...");
        // Trapped
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.WALL_N,
                CharacteristicLabels.WALL_E,
                CharacteristicLabels.WALL_S,
                CharacteristicLabels.WALL_W },
                Action.DO_NOTHING);
        // Corners
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_N,
                CharacteristicLabels.WALL_NW,
                CharacteristicLabels.LOOKING_EAST },
                Action.MOVE_NORTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_E,
                CharacteristicLabels.WALL_NE,
                CharacteristicLabels.LOOKING_SOUTH },
                Action.MOVE_EAST);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_S,
                CharacteristicLabels.WALL_SE,
                CharacteristicLabels.LOOKING_WEST },
                Action.MOVE_SOUTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_W,
                CharacteristicLabels.WALL_SW,
                CharacteristicLabels.LOOKING_NORTH },
                Action.MOVE_WEST);
        // Movements to conservate looking direction
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_N,
                CharacteristicLabels.WALL_W,
                CharacteristicLabels.LOOKING_NORTH },
                Action.MOVE_NORTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_E,
                CharacteristicLabels.WALL_N,
                CharacteristicLabels.LOOKING_EAST },
                Action.MOVE_EAST);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_S,
                CharacteristicLabels.WALL_E,
                CharacteristicLabels.LOOKING_SOUTH },
                Action.MOVE_SOUTH);
        this.addRule(new CharacteristicLabels[] {
                CharacteristicLabels.NOT_WALL_W,
                CharacteristicLabels.WALL_S,
                CharacteristicLabels.LOOKING_WEST },
                Action.MOVE_WEST);

        // Basic movements
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.NOT_WALL_N,
                CharacteristicLabels.WALL_W },
                Action.MOVE_NORTH);
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.NOT_WALL_E,
                CharacteristicLabels.WALL_N },
                Action.MOVE_EAST);
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.NOT_WALL_S,
                CharacteristicLabels.WALL_E },
                Action.MOVE_SOUTH);
        this.addRule(new CharacteristicLabels[] { CharacteristicLabels.NOT_WALL_W,
                CharacteristicLabels.WALL_S },
                Action.MOVE_WEST);

        // Default action
        this.addRule(new CharacteristicLabels[] {}, Action.MOVE_NORTH);
        FileLogger.info("[ROBOT] BC initialized");
    }

    @Override
    public void processInputSensors(boolean[] sensors) {
        FileLogger.info("[ROBOT] Processing input sensors... (" + Arrays.toString(sensors) + ")");
        for (int i = 0; i < sensors.length; i++) {
            characteristics[i * 2].setValue(sensors[i]);
            characteristics[(i * 2) + 1].setValue(!sensors[i]);
        }

        characteristics[16].setValue(false);
        characteristics[17].setValue(false);
        characteristics[18].setValue(false);
        characteristics[19].setValue(false);
        characteristics[characteristics.length - (4 - this.direction.ordinal())].setValue(true);
        FileLogger.info("[ROBOT] Input sensors processed, new characteristics are: "
                + Arrays.toString(characteristics));
    }

}
