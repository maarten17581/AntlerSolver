package thesis.antlersolver.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import thesis.antlersolver.algorithm.GraphAlgorithm;

public class FVC {
    private Set<Node> A;
    private Set<Node> C;
    private Set<Node> F;
    public Graph graph;

    private static Map<Integer, Integer> hashTableA = new HashMap<>();
    private static Map<Integer, Integer> hashTableC = new HashMap<>();
    private static Map<Integer, Integer> hashTableF = new HashMap<>();
    private static Random rand = new Random();

    public int hash = 0;

    private void updateHash(Node v, char set) {
        if(set == 'A') {
            if(hashTableA.get(v.id) == null) {
                hashTableA.put(v.id, rand.nextInt());
            }
            hash ^= hashTableA.get(v.id);
        } else if(set == 'C') {
            if(hashTableC.get(v.id) == null) {
                hashTableC.put(v.id, rand.nextInt());
            }
            hash ^= hashTableC.get(v.id);
        } else if(set == 'F') {
            if(hashTableF.get(v.id) == null) {
                hashTableF.put(v.id, rand.nextInt());
            }
            hash ^= hashTableF.get(v.id);
        }
    }

    public Set<Node> getA() {
        return A;
    }

    public Set<Node> getC() {
        return C;
    }

    public Set<Node> getF() {
        return F;
    }

    public void addA(Node v) {
        if(A.add(v)) {
            updateHash(v, 'A');
        }
    }

    public void addC(Node v) {
        if(C.add(v)) {
            updateHash(v, 'C');
        }
    }

    public void addF(Node v) {
        if(F.add(v)) {
            updateHash(v, 'F');
        }
    }

    public void removeA(Node v) {
        if(A.remove(v)) {
            updateHash(v, 'A');
        }
    }

    public void removeC(Node v) {
        if(C.remove(v)) {
            updateHash(v, 'C');
        }
    }

    public void removeF(Node v) {
        if(F.remove(v)) {
            updateHash(v, 'F');
        }
    }

    public FVC(Graph graph) {
        this.graph = graph;
        A = new HashSet<>();
        C = new HashSet<>();
        F = new HashSet<>();
    }

    public FVC(Graph graph, Set<Node> C) {
        this(graph);
        for(Node v : C) {
            addC(v);
        }
        setMaxF();
    }

    public void setMaxF() {
        for(Node v : GraphAlgorithm.getF(new ArrayList<>(C), graph)) {
            addF(v);
        }
    }

    public void computeMaxA(boolean onlyFlower) {
        A.clear();
        Graph fvsGraph = new Graph(graph.name+":FVC");
        for(Node v : C) {
            fvsGraph.addNode(v.id);
        }
        for(Node v : F) {
            fvsGraph.addNode(v.id);
        }
        for(Node v : C) {
            for(Edge e : v.neighbors.values()) {
                if(e.t.id < v.id || !(C.contains(e.t) || F.contains(e.t))) continue;
                fvsGraph.addEdge(v.id, e.t.id, e.c);
            }
        }
        for(Node v : F) {
            for(Edge e : v.neighbors.values()) {
                if(e.t.id < v.id || !(C.contains(e.t) || F.contains(e.t))) continue;
                fvsGraph.addEdge(v.id, e.t.id, e.c);
            }
        }
        for(Node v : C) {
            if(GraphAlgorithm.hasFlower(F, v) >= C.size()) {
                addA(v);
            } else if(!onlyFlower && GraphAlgorithm.smartDisjointFVS(fvsGraph.nodes.get(v.id), C.size()-1, fvsGraph) == null) {
                addA(v);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof PathAntler)) return false;
        return hash == ((PathAntler)other).hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return "A: "+(new ArrayList<Node>(A)).toString()+", C: "+(new ArrayList<Node>(C)).toString()+", F: "+(new ArrayList<Node>(F));
    }
}
