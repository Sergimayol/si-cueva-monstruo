package agent;

import java.awt.Point;

import agent.rules.BC;
import agent.rules.Characteristic;
import agent.rules.Condition;
import agent.rules.Rule;

public abstract class GridAgent<T> {

    protected Characteristic[] characteristics;
    protected BC<T> bc;

    public void addProdRule(Rule<T> rule) {
        this.bc.addProdRule(rule);
    }

    public void addProdRule(int[] indices, T action) {
        this.bc.addProdRule(new Rule<>(new Condition(selectCharacteristics(this.characteristics, indices)), action));
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

    public abstract void processPerceptions(boolean[] perceptions);

    public void setCharacteristics(Characteristic[] characteristics) {
        this.characteristics = characteristics;
    }

    public Characteristic[] getCharacteristics() {
        return this.characteristics;
    }

    // ==========================================================================
    private Characteristic[] selectCharacteristics(Characteristic[] characteristics, int[] indices) {
        Characteristic[] resultCharacteristics = new Characteristic[indices.length];
        for (int i = 0; i < indices.length; i++) {
            resultCharacteristics[i] = characteristics[indices[i]];
        }
        return resultCharacteristics;
    }
}
