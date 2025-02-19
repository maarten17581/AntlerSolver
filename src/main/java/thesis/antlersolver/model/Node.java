package thesis.antlersolver.model;

import java.util.HashMap;
import java.util.Map;

public class Node {
    public int id;
    public Map<Node, Edge> neighbors;
    public int nbhSize;
    public int degree;

    public Node(int id) {
        this.id = id;
        neighbors = new HashMap<>();
        nbhSize = 0;
        degree = 0;
    }

    public Edge addNeighbor(Node t, int c) {
        degree += c;
        if(t != this) {
            t.degree += c;
        }
        if(neighbors.get(t) == null) {
            neighbors.put(t, new Edge(this, t, c));
            if(t != this) {
                nbhSize++;
                t.nbhSize++;
                t.neighbors.put(this, new Edge(t, this, c));
                neighbors.get(t).backEdge = t.neighbors.get(this);
                t.neighbors.get(this).backEdge = neighbors.get(t);
            } else {
                neighbors.get(t).backEdge = neighbors.get(t);
            }
        } else {
            neighbors.get(t).c += c;
            if(t != this) {
                t.neighbors.get(this).c += c;
            }
        }
        return neighbors.get(t);
    }

    public Edge addNeighbor(Edge e) {
        degree += e.c;
        if(e.t != this) {
            e.t.degree += e.c;
        }
        if(neighbors.get(e.t) == null) {
            neighbors.put(e.t, new Edge(this, e.t, e.c));
            if(e.t != this) {
                nbhSize++;
                e.t.nbhSize++;
                e.t.neighbors.put(this, new Edge(e.t, this, e.c));
                neighbors.get(e.t).backEdge = e.t.neighbors.get(this);
                e.t.neighbors.get(this).backEdge = neighbors.get(e.t);
            } else {
                neighbors.get(e.t).backEdge = neighbors.get(e.t);
            }
        } else {
            neighbors.get(e.t).c += e.c;
            if(e.t != this) {
                e.t.neighbors.get(this).c += e.c;
            }
        }
        return neighbors.get(e.t);
    }

    public Edge removeNeighbor(Node t, int c) {
        if(neighbors.get(t) == null || neighbors.get(t).c < c) {
            return null;
        }
        degree -= c;
        neighbors.get(t).c -= c;
        if(t != this) {
            t.degree -= c;
            t.neighbors.get(this).c -= c;
        }
        if(neighbors.get(t).c == 0) {
            if(t != this) {
                nbhSize--;
                t.nbhSize--;
                t.neighbors.remove(this);
            }
            return neighbors.remove(t);
        } else {
            return neighbors.get(t);
        }
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
