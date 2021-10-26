package Entity;

public class Variable {
    private boolean isBoolean;
    private String name;
    private int value;

    public Variable() {
        super();
    }

    @Override
    public String toString() {
        return this.name + String.valueOf(value);
    }
}
