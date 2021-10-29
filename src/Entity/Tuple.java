package Entity;

public class Tuple<X, Y> {
    public Character x;
    public Integer y;

    public Tuple(Character x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Tuple() {

    }

    public Character getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
}
