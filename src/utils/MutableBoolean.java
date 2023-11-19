package utils;

import java.io.Serializable;

public class MutableBoolean implements Serializable {
    private boolean value;

    public MutableBoolean() {
        value = false;
    }

    public MutableBoolean(boolean value) {
        this.value = value;
    }

    public boolean is() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

}
