package Entity;

public class Variable {
    private boolean isBoolean;
    private String name;
    private int value;

    public boolean isBoolean() {
        return isBoolean;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public boolean equals(Variable v) {
        return name.equals(v.name) && value == v.value;
    }

    public String toString(Boolean isPost) {
        if ( !isPost )
            return name+"="+value;
        else
            return name+"'="+value;
    }

    @Override
    public String toString(){
        return toString(false);
    }
}
