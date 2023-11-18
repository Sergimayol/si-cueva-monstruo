package productionrules;

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
        String repr = "";
        for (int i = 0; i < characteristics.length; i++) {
            repr += characteristics[i];
            if (i < characteristics.length - 1) {
                repr += " AND ";
            }
        }
        return repr;
    }
}
