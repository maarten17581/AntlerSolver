package thesis.antlersolver.model;

import java.util.Map;

public class Edge {
    Node s;
    Node t;
    int c;
    Edge backEdge;

    public Edge(Node s, Node t) {
        this.s = s;
        this.t = t;
        c = 1;
    }

    public Edge(Node s, Node t, int c) {
        this.s = s;
        this.t = t;
        this.c = c;
    }

    @Override
    public String toString() {
        return "s: "+s.id+", t: "+t.id+", c: "+c+"\n";
    }
}
