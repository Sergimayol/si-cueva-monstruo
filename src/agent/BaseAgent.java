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

    public void addRule(CharacteristicLabels[] indices, T action) {
        int[] indicesInt = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            indicesInt[i] = indices[i].ordinal();
        }
        this.bc.addProdRule(new Rule<>(new Condition(selectCharacteristics(this.characteristics, indicesInt)), action));
    }

    public void updateFacts(Point position) {
        this.bc.updateFacts(position, this.characteristics);
    }

    public BC<T> inferBC(Point currentPos) {
        return this.bc.infer(currentPos, this.characteristics);
    }

    public T checkBC() {
        return this.bc.getAction();
    }

    public String printBC() {
        return this.bc.toString();
    }

    public String printEvaluatedBC() {
        return this.bc.toStringEvaluated();
    }

    public abstract void processInputSensors(boolean[] sensors);

    public void setCharacteristics(Characteristic[] characteristics) {
        this.characteristics = characteristics;
    }

    public Characteristic[] getCharacteristics() {
        return this.characteristics;
    }

    private Characteristic[] selectCharacteristics(Characteristic[] characteristics, int[] indices) {
        Characteristic[] resultCharacteristics = new Characteristic[indices.length];
        for (int i = 0; i < indices.length; i++) {
            resultCharacteristics[i] = characteristics[indices[i]];
        }
        return resultCharacteristics;
    }
}
