package agent.rules;

public class Condition {

    private Characteristic[] characteristics;

    public Condition() {
        characteristics = new Characteristic[0];
    }

    public Condition(Characteristic[] characteristics) {
        this.characteristics = characteristics;
    }

    public boolean eval() {
        for (Characteristic c : characteristics) {
            if (!c.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < characteristics.length; i++) {
            sb.append(characteristics[i]);
            if (i < characteristics.length - 1) {
                sb.append(" AND ");
            }
        }
        return sb.toString();
    }
}
