package thesis.antlersolver.model;

import java.util.HashMap;
import java.util.Map;

public class Graph {
    String name;
    private int nodeIds;
    int nodecount;
    int edgecount;
    Map<Integer, Node> nodes;

    public Graph(String name) {
        this.name = name;
        nodeIds = 0;
        nodecount = 0;
        edgecount = 0;
        nodes = new HashMap<>();
    }

    public boolean addNode(int id) {
        if(nodes.get(id) == null) {
            nodes.put(id, new Node(id));
            nodecount++;
            nodeIds = Math.max(nodeIds, id+1);
            return true;
        }
        return false;
    }

    public int addNode() {
        addNode(nodeIds);
        return nodeIds-1;
    }

    public boolean removeNode(int id) {
        if(nodes.get(id) != null) {
            nodes.remove(id);
            nodecount--;
            return true;
        }
        return false;
    }

    public void addEdge(int sid, int tid) {
        addEdge(sid, tid, 1);
    }

    public void addEdge(int sid, int tid, int c) {
        Node s = nodes.get(sid);
        Node t = nodes.get(tid);
        if (s != null && t != null) {
            if(s.neighbors.get(t) != null) {
                s.neighbors.get(t).c += c;
                t.neighbors.get(s).c += c;
            } else {
                Edge e1 = new Edge(s, t, c);
                Edge e2 = new Edge(t, s, c);
                e1.backEdge = e2;
                e2.backEdge = e1;
                s.addNeighbor(e1);
                t.addNeighbor(e2);
                s.nbhSize++;
                t.nbhSize++;
            }
            s.degree += c;
            t.degree += c;
        }
        edgecount += c;
    }

    public boolean removeEdge(int sid, int tid) {
        return removeEdge(sid, tid, 1);
    }

    public boolean removeAllEdges(int sid, int tid) {
        Node s = nodes.get(sid);
        Node t = nodes.get(tid);
        Edge e = s.neighbors.get(t);
        if(e == null) {
            return false;
        }
        return removeEdge(sid, tid, e.c);
    }

    public boolean removeEdge(int sid, int tid, int c) {
        Node s = nodes.get(sid);
        Node t = nodes.get(tid);
        Edge e1 = s.neighbors.get(t);
        Edge e2 = t.neighbors.get(s);
        if(e1 == null || e1.c < c) {
            return false;
        }
        e1.c -= c;
        e2.c -= c;
        s.degree -= c;
        t.degree -= c;
        edgecount -= c;
        if(e1.c == 0) {
            s.neighbors.remove(t);
            t.neighbors.remove(s);
            s.nbhSize--;
            t.nbhSize--;
        }
        return true;
    }

    @Override
    public String toString() {
        String toPrint = "Graph: "+name+"\n";
        toPrint += "Nodes: "+nodecount+"\n";
        toPrint += "Edges: "+edgecount+"\n";
        for(Map.Entry<Integer, Node> entry : nodes.entrySet()) {
            toPrint += entry.getValue().toString();
        }
        toPrint += "##########";
        return toPrint;
    }
}
