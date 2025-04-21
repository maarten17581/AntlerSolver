package thesis.antlersolver.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    public Set<Edge> betweenF;
    public Set<Edge> doubleToF;

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
        betweenF = new HashSet<>();
        doubleToF = new HashSet<>();
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

    public Node addNode(Node v) {
        if(nodes.get(v.id) == null && v.neighbors.isEmpty()) {
            nodes.put(v.id, v);
            nodecount++;
            nodeIds = Math.max(nodeIds, v.id+1);
            nodeSetUpdate(v);
            return v;
        }
        return null;
    }

    public boolean addNodeSet(List<Node> nodeSet) {
        for(Node v : nodeSet) {
            if(nodes.get(v.id) != null) return false;
            for(Edge e : v.neighbors.values()) {
                if(!nodeSet.contains(e.t) || e.t.neighbors.get(v) != e.backEdge) {
                    return false;
                }
            }
        }
        nodecount += nodeSet.size();
        int totalDegree = 0;
        for(Node v : nodeSet) {
            nodes.put(v.id, v);
            totalDegree += v.degree;
        }
        edgecount += totalDegree/2;
        for(Node v : nodeSet) {
            nodeIds = Math.max(nodeIds, v.id+1);
            for(Edge e : v.neighbors.values()) {
                edgeSetUpdate(e);
            }
        }
        return true;
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
            edgeSetUpdate(e);
        }
        return e;
    }

    public void addToF(int id) {
        nodes.get(id).setToF();
        for(Edge e : nodes.get(id).neighbors.values()) {
            if(e.t.isF() && e.c <= 1) {
                betweenF.add(e);
            }
            if(e.c >= 2) {
                doubleToF.add(e);
            }
        }
    }

    public void addToF(Node v) {
        v.setToF();
        for(Edge e : v.neighbors.values()) {
            if(e.t.isF() && e.c <= 1) {
                betweenF.add(e);
            }
            if(e.c >= 2) {
                doubleToF.add(e);
            }
        }
    }

    public void removeFromF(int id) {
        nodes.get(id).removeFromF();
        for(Edge e : nodes.get(id).neighbors.values()) {
            if(e.t.isF()) {
                betweenF.remove(e);
                betweenF.remove(e.backEdge);
            }
            if(e.c >= 2) {
                doubleToF.remove(e);
                doubleToF.remove(e.backEdge);
            }
        }
    }

    public void removeFromF(Node v) {
        v.removeFromF();
        for(Edge e : v.neighbors.values()) {
            if(e.t.isF()) {
                betweenF.remove(e);
                betweenF.remove(e.backEdge);
            }
            if(e.c >= 2) {
                doubleToF.remove(e);
                doubleToF.remove(e.backEdge);
            }
        }
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
        if(v.degree == 1 && !selfloop.contains(v)) {
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
            Edge[] neighbors = v.neighbors.values().toArray(new Edge[0]);
            Node w = neighbors[0].c > neighbors[1].c ? neighbors[0].t : neighbors[1].t;
            if(w.isF()) {
                singleAntler.remove(v);
            } else {
                singleAntler.add(v);
            }
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
            multiEdge.remove(e.backEdge);
        }
        if(e.c > 2) {
            multiEdge.add(e);
            multiEdge.add(e.backEdge);
        }
        if(betweenF.contains(e) && e.c != 1) {
            betweenF.remove(e);
        }
        if(betweenF.contains(e.backEdge) && e.backEdge.c != 1) {
            betweenF.remove(e.backEdge);
        }
        if(e.c == 1 && e.s.isF() && e.t.isF() && e.s != e.t && !betweenF.contains(e.backEdge)) {
            betweenF.add(e);
        }
        if(doubleToF.contains(e) && e.c < 2) {
            doubleToF.remove(e);
        }
        if(doubleToF.contains(e.backEdge) && e.backEdge.c < 2) {
            doubleToF.remove(e.backEdge);
        }
        if((e.s.isF() && !e.t.isF() || !e.s.isF() && e.t.isF()) && e.c >= 2 && !doubleToF.contains(e.backEdge)) {
            doubleToF.add(e);
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
