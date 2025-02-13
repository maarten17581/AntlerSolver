package thesis.antlersolver.model;

import java.util.HashMap;
import java.util.Map;

public class Node {
    int id;
    Map<Node, Edge> neighbors;
    int nbhSize;
    int degree;

    public Node(int id) {
        this.id = id;
        neighbors = new HashMap<>();
        nbhSize = 0;
        degree = 0;
    }

    public void addNeighbor(Edge e) {
        neighbors.put(e.t, e);
    }

    @Override
    public String toString() {
        String toPrint = "----------\n";
        toPrint += "Node: "+id+"\n";
        toPrint += "NbhSize: "+nbhSize+"\n";
        toPrint += "Degree: "+degree+"\n";
        for(Map.Entry<Node, Edge> entry : neighbors.entrySet()) {
            toPrint += "    "+entry.getValue().toString();
        }
        return toPrint;
    }
}
