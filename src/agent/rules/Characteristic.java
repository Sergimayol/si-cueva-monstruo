package agent.rules;

public class Characteristic {
    private boolean value = true;
    private String label;

    public Characteristic(String label) {
        this.label = label;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return label + ": " + value;
    }

}