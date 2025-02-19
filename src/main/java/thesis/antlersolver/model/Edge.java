package thesis.antlersolver.model;

public class Edge {
    public Node s;
    public Node t;
    public int c;
    public Edge backEdge;

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
