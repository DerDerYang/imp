package Entity;

import java.util.ArrayList;

public class Variable {
    private boolean isBoolean;
    private char name;
    private int value;


    public Variable() {
        this.isBoolean = false;
    }

    public Variable(boolean isBoolean, char name, int value) {
        this.isBoolean = isBoolean;
        this.name = name;
        this.value = value;
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    public char getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public boolean equals(Variable v) {
        return name == v.name && value == v.value;
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

    public static void changeValue(ArrayList<Variable> vars, char var, int value) {
        boolean find = false;
        for (Variable v : vars) {
            if (v.getName() == var) {
                v.value = value;
                find = true;
                break;
            }
        }
        if (!find)
            vars.add(new Variable(false,var,value));
    }
}
