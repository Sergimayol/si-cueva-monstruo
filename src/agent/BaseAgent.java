package agent;

import java.awt.Point;

import agent.labels.CharacteristicLabels;
import agent.rules.BC;
import agent.rules.Characteristic;
import agent.rules.Condition;
import agent.rules.Rule;

public abstract class BaseAgent<T> {

    protected Characteristic[] characteristics;
    protected BC<T> bc;
    protected Point position;

    public void addRule(Rule<T> rule) {
        this.bc.addProdRule(rule);
    }

    public void addRule(CharacteristicLabels[] indices, T action) {
        int[] indicesInt = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            indicesInt[i] = indices[i].ordinal();
        }
        this.bc.addProdRule(new Rule<>(new Condition(selectCharacteristics(this.characteristics, indicesInt)), action));
    }

    public T checkBC() {
        return this.bc.check();
    }

    public String printBC() {
        return this.bc.toString();
    }

    public String printEvaluatedBC() {
        return this.bc.toStringEvaluated();
    }

    public abstract void processInputSensors(boolean[] perceptions);

    public void setCharacteristics(Characteristic[] characteristics) {
        this.characteristics = characteristics;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setPosition(int x, int y) {
        this.position = new Point(x, y);
    }

    private Characteristic[] selectCharacteristics(Characteristic[] characteristics, int[] indices) {
        Characteristic[] resultCharacteristics = new Characteristic[indices.length];
        for (int i = 0; i < indices.length; i++) {
            resultCharacteristics[i] = characteristics[indices[i]];
        }
        return resultCharacteristics;
    }
}
