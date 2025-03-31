package thesis.antlersolver.model;

public class Pair<A,B> {
    public final A a;
    public final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Pair<?,?>)) return false;
        return a == ((Pair<?,?>)other).a && b == ((Pair<?,?>)other).b;
    }

    @Override
    public int hashCode() {
        return 1000000007*a.hashCode()+b.hashCode();
    }

    @Override
    public String toString() {
        return "("+a.toString()+", "+b.toString()+")";
    }
}
