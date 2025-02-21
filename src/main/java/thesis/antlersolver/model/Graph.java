package thesis.antlersolver.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Graph {
    public String name;
    public int nodeIds;
    public int nodecount;
    public int edgecount;
    public Map<Integer, Node> nodes;
    public Set<Node> isolated;
    public Set<Node> leaves;
    public Set<Node> degree2;
    public Set<Node> selfloop;
    public Set<Node> singleAntler;
    public Set<Edge> multiEdge;

    public Graph(String name) {
        this.name = name;
        nodeIds = 0;
        nodecount = 0;
        edgecount = 0;
        nodes = new HashMap<>();
        isolated = new HashSet<>();
        leaves = new HashSet<>();
        degree2 = new HashSet<>();
        selfloop = new HashSet<>();
        singleAntler = new HashSet<>();
        multiEdge = new HashSet<>();
    }

    public Graph(String name, int size) {
        this(name);
        for(int i = 0; i < size; i++) {
            addNode();
        }
    }

    public Graph(String name, int size, double p) {
        this(name, size);
        Random random = new Random();
        for(int i = 0; i < size; i++) {
            for(int j = i+1; j < size; j++) {
                if(random.nextDouble() < p) {
                    addEdge(i, j);
                }
            }
        }
    }

    public Node addNode(int id) {
        if(nodes.get(id) == null) {
            nodes.put(id, new Node(id));
            nodecount++;
            nodeIds = Math.max(nodeIds, id+1);
            nodeSetUpdate(nodes.get(id));
            return nodes.get(id);
        }
        return null;
    }

    public Node addNode() {
        return addNode(nodeIds);
    }

    public Node removeNode(int id) {
        if(nodes.get(id) == null || !nodes.get(id).neighbors.isEmpty()) {
            return null;
        }
        nodecount--;
        isolated.remove(nodes.get(id));
        return nodes.remove(id);
    }

    public Edge addEdge(int sid, int tid) {
        return addEdge(sid, tid, 1);
    }

    public Edge addEdge(Edge e) {
        if(nodes.get(e.s.id) == null || nodes.get(e.t.id) == null) {
            return null;
        }
        edgecount += e.c;
        Edge e2 = nodes.get(e.s.id).addNeighbor(e);
        edgeSetUpdate(e2);
        return e2;
    }

    public Edge addEdge(int sid, int tid, int c) {
        Node s = nodes.get(sid);
        Node t = nodes.get(tid);
        if(s == null || t == null) {
            return null;
        }
        edgecount += c;
        Edge e = s.addNeighbor(t, c);
        edgeSetUpdate(e);
        return e;
    }

    public Edge removeEdge(int sid, int tid) {
        return removeEdge(sid, tid, 1);
    }

    public Edge removeAllEdges(int sid, int tid) {
        Node s = nodes.get(sid);
        Node t = nodes.get(tid);
        Edge e = s.neighbors.get(t);
        return removeEdge(sid, tid, e.c);
    }

    public Edge removeEdge(int sid, int tid, int c) {
        Node s = nodes.get(sid);
        Node t = nodes.get(tid);
        if(s == null || t == null) {
            return null;
        }
        Edge e = s.removeNeighbor(t, c);
        if(e != null) {
            edgecount -= c;
            nodeSetUpdate(e.s);
            nodeSetUpdate(e.t);
            edgeSetUpdate(e);
        }
        return e;
    }

    private void nodeSetUpdate(Node v) {
        if(isolated.contains(v) && v.degree != 0) {
            isolated.remove(v);
        }
        if(v.neighbors.isEmpty()) {
            isolated.add(v);
        }
        if(leaves.contains(v) && v.degree != 1) {
            leaves.remove(v);
        }
        if(v.degree == 1) {
            leaves.add(v);
        }
        if(degree2.contains(v) && (v.degree != 2 || selfloop.contains(v))) {
            degree2.remove(v);
        }
        if(v.degree == 2 && !selfloop.contains(v)) {
            degree2.add(v);
        }
        if(singleAntler.contains(v) && !(v.degree == 3 && v.nbhSize == 2 && !selfloop.contains(v))) {
            singleAntler.remove(v);
        }
        if(v.degree == 3 && v.nbhSize == 2 && !selfloop.contains(v)) {
            singleAntler.add(v);
        }
    }

    private void edgeSetUpdate(Edge e) {
        if(e.s == e.t && e.c != 0) {
            selfloop.add(e.s);
        }
        if(e.s == e.t && e.c == 0) {
            selfloop.remove(e.s);
        }
        if(multiEdge.contains(e) && e.c <= 2) {
            multiEdge.remove(e);
        }
        if(e.c > 2) {
            multiEdge.add(e);
        }
        nodeSetUpdate(e.s);
        nodeSetUpdate(e.t);
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
