package thesis.antlersolver.model;

import java.util.Map;

public class Pair<A,B> {
    public final A a;
    public final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "("+a.toString()+", "+b.toString()+")";
    }
}
