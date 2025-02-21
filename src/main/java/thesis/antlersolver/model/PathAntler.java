package thesis.antlersolver.model;

import java.util.ArrayList;
import java.util.List;

public class PathAntler {
    public List<Node> C;
    public List<Node> P;
    public Graph graph;

    public PathAntler(Graph graph) {
        this.graph = graph;
        C = new ArrayList<>();
        P = new ArrayList<>();
    }
}
