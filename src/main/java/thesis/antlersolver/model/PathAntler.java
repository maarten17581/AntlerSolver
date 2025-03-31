package thesis.antlersolver.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import thesis.antlersolver.algorithm.GraphAlgorithm;

public class PathAntler {
    private Set<Node> A;
    private Set<Node> C;
    private Set<Node> P;
    public Graph graph;
    public Node[] endpoints;
    public Node[] nextnodes;
    public boolean[] extended;
    public boolean isCyclic;

    private static Map<Integer, Integer> hashTableA = new HashMap<>();
    private static Map<Integer, Integer> hashTableC = new HashMap<>();
    private static Map<Integer, Integer> hashTableP = new HashMap<>();
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
        } else if(set == 'P') {
            if(hashTableP.get(v.id) == null) {
                hashTableP.put(v.id, rand.nextInt());
            }
            hash ^= hashTableP.get(v.id);
        }
    }

    public Set<Node> getA() {
        return A;
    }

    public Set<Node> getC() {
        return C;
    }

    public Set<Node> getP() {
        return P;
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

    public void addP(Node v) {
        if(P.add(v)) {
            updateHash(v, 'P');
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

    public void removeP(Node v) {
        if(P.remove(v)) {
            updateHash(v, 'P');
        }
    }

    public PathAntler(Graph graph) {
        this.graph = graph;
        A = new HashSet<>();
        C = new HashSet<>();
        P = new HashSet<>();
        endpoints = new Node[2];
        nextnodes = new Node[2];
        extended = new boolean[2];
        isCyclic = false;
    }

    public void computeStatistics() {
        nextnodes[0] = null;
        nextnodes[1] = null;
        endpoints[0] = null;
        endpoints[1] = null;
        isCyclic = false;
        for(Node pathNode : P) {
            int pCount = 0;
            Node next = null;
            Node next2 = null;
            for(Node nb : pathNode.neighbors.keySet()) {
                if(!P.contains(nb) && !C.contains(nb)) {
                    if(next == null) {
                        next = nb;
                    } else {
                        next2 = nb;
                    }
                }
                if(P.contains(nb)) {
                    pCount++;
                }
            }
            if(pCount <= 1) {
                if(endpoints[0] == null) {
                    endpoints[0] = pathNode;
                    nextnodes[0] = next;
                    endpoints[1] = pathNode;
                    nextnodes[1] = next2;
                } else {
                    endpoints[1] = pathNode;
                    nextnodes[1] = next;
                }
            }
        }
        if(endpoints[0] == null) {
            isCyclic = true;
        }
    }

    public void extendP(boolean earlyStop) {
        for(int i = 0; i < 2; i++) {
            if(nextnodes[i] == null) continue;
            while(true) {
                Node step = null;
                int pConnect = 0;
                int stepConnect = 0;
                for(Edge e : nextnodes[i].neighbors.values()) {
                    if(C.contains(e.t)) continue;
                    if(P.contains(e.t)) {
                        pConnect++;
                        continue;
                    }
                    if(e.c == 1) {
                        step = e.t;
                    }
                    stepConnect += e.c;
                }
                if((pConnect == 2 && stepConnect > 0) || stepConnect > 1 || step == nextnodes[i]) break;
                if(pConnect == 2) {
                    addP(nextnodes[i]);
                    endpoints[0] = null;
                    endpoints[1] = null;
                    nextnodes[0] = null;
                    nextnodes[1] = null;
                    isCyclic = true;
                    break;
                }
                addP(nextnodes[i]);
                endpoints[i] = nextnodes[i];
                nextnodes[i] = step;
                if(stepConnect == 0) break;
                if(earlyStop && P.size() > C.size()*(2*C.size() + 1)) break;
            }
        }
    }

    public void computeMaxA() {
        A.clear();
        Graph pathAntlerGraph = new Graph(graph.name+":pathAntler");
        for(Node v : C) {
            pathAntlerGraph.addNode(v.id);
        }
        for(Node v : P) {
            pathAntlerGraph.addNode(v.id);
        }
        for(Node v : C) {
            for(Edge e : v.neighbors.values()) {
                if(e.t.id < v.id || !(C.contains(e.t) || P.contains(e.t))) continue;
                pathAntlerGraph.addEdge(v.id, e.t.id, e.c);
            }
        }
        for(Node v : P) {
            for(Edge e : v.neighbors.values()) {
                if(e.t.id < v.id || !(C.contains(e.t) || P.contains(e.t))) continue;
                pathAntlerGraph.addEdge(v.id, e.t.id, e.c);
            }
        }
        for(Node v : C) {
            if(GraphAlgorithm.hasFlower(P, v) >= C.size()+1) {
                addA(v);
            } else if(GraphAlgorithm.smartDisjointFVS(pathAntlerGraph.nodes.get(v.id), C.size(), pathAntlerGraph) == null) {
                addA(v);
            }
        }
    }

    public boolean contained(PathAntler other) {
        for(Node v : C) {
            if(!other.C.contains(v)) return false;
        }
        for(Node v : other.P) {
            if(!P.contains(v)) return false;
        }
        return true;
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
        return "A: "+(new ArrayList<Node>(A)).toString()+", C: "+(new ArrayList<Node>(C)).toString()+", P: "+(new ArrayList<Node>(P));
    }
}