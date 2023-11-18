package agent.rules;

public class Rule<T> {

    private Condition condition;
    private T action;

    public Rule() {
        condition = new Condition();
        action = null;
    }

    public Rule(Condition condition, T action) {
        this.condition = condition;
        this.action = action;
    }

    public boolean eval() {
        return condition.eval();
    }

    public T getAction() {
        return action;
    }

    @Override
    public String toString() {
        if (action == null) {
            return condition.toString() + " -> pass";
        }
        return condition.toString() + " -> " + action.toString();
    }

}
