package thesis.antlersolver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fvs_wata_orz.Graph;
import fvs_wata_orz.tc.wata.util.Utils;
import thesis.antlersolver.algorithm.GraphAlgorithm;

public class PathAntler {
    private int[] A;
    private int[] C;
    private int[] P;
    public Graph graph;
    public int[] endpoints;
    public int[] nextnodes;
    public boolean[] extended;
    public boolean isCyclic;

    public int[] getA() {
        return A;
    }

    public int[] getC() {
        return C;
    }

    public int[] getP() {
        return P;
    }

    private void add(int v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'P') {
            S = P;
        }
        int i = Utils.upperBound(S, v);
		if (i >= 1 && S[i - 1] == v) return;
		int[] a = new int[S.length + 1];
		System.arraycopy(S, 0, a, 0, i);
		a[i] = v;
		System.arraycopy(S, i, a, i + 1, S.length - i);
        if(set == 'A') {
            A = a;
        } else if(set == 'C') {
            C = a;
        } else if(set == 'P') {
            P = a;
        }
    }

    private void add(int[] v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'P') {
            S = P;
        }
        int[] a = new int[S.length + v.length];
        int i = 0;
        int j = 0;
        while(i < S.length && j < v.length) {
            if(S[i] <= v[j]) {
                a[i+j] = S[i];
                i++;
            } else if(S[i] > v[j]) {
                a[i+j] = v[j];
                j++;
            }
        }
        while(i < S.length) {
            a[i+j] = S[i];
            i++;
        }
        while(j < v.length) {
            a[i+j] = v[j];
            j++;
        }
        if(set == 'A') {
            A = a;
        } else if(set == 'C') {
            C = a;
        } else if(set == 'P') {
            P = a;
        }
    }

    private void remove(int v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'P') {
            S = P;
        }
        int i = Arrays.binarySearch(S, v);
		int[] a = new int[S.length - 1];
		System.arraycopy(S, 0, a, 0, i);
		System.arraycopy(S, i + 1, a, i, a.length - i);
		S = a;
        if(set == 'A') {
            A = a;
        } else if(set == 'C') {
            C = a;
        } else if(set == 'P') {
            P = a;
        }
    }

    public void addA(int v) {
        add(v, 'A');
    }

    public void addA(int[] v) {
        add(v, 'A');
    }

    public void addC(int v) {
        add(v, 'C');
    }

    public void addC(int[] v) {
        add(v, 'C');
    }

    public void addP(int v) {
        add(v, 'P');
    }

    public void addP(int[] v) {
        add(v, 'P');
    }

    public void removeA(int v) {
        remove(v, 'A');
    }

    public void removeC(int v) {
        remove(v, 'C');
    }

    public void removeP(int v) {
        remove(v, 'P');
    }

    private boolean setContained(int v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'P') {
            S = P;
        }
        return Arrays.binarySearch(S, v) >= 0;
    }

    public PathAntler(Graph graph) {
        this.graph = graph;
        A = new int[0];
        C = new int[0];
        P = new int[0];
        endpoints = new int[]{-1, -1};
        nextnodes = new int[]{-1, -1};
        extended = new boolean[2];
        isCyclic = false;
    }

    public PathAntler(Graph graph, int[] A, int[] C, int[] P) {
        this.graph = graph;
        this.A = A;
        this.C = C;
        this.P = P;
        endpoints = new int[]{-1, -1};
        nextnodes = new int[]{-1, -1};
        extended = new boolean[2];
        isCyclic = false;
        computeStatistics();
    }

    public PathAntler(PathAntler pathAntler) {
        this.graph = pathAntler.graph;
        this.A = pathAntler.A.clone();
        this.C = pathAntler.C.clone();
        this.P = pathAntler.P.clone();
        this.endpoints = pathAntler.endpoints.clone();
        this.nextnodes = pathAntler.nextnodes.clone();
        this.extended = pathAntler.extended.clone();
        isCyclic = pathAntler.isCyclic;
    }

    public void computeStatistics() {
        nextnodes[0] = -1;
        nextnodes[1] = -1;
        endpoints[0] = -1;
        endpoints[1] = -1;
        isCyclic = false;
        for(int pathNode : P) {
            int pCount = 0;
            int next = -1;
            int next2 = -1;
            for(int nb : graph.adj[pathNode]) {
                if(!setContained(nb, 'P') && !setContained(nb, 'C')) {
                    if(next == -1) {
                        next = nb;
                    } else {
                        next2 = nb;
                    }
                }
                if(setContained(nb, 'P')) {
                    pCount++;
                }
            }
            if(pCount <= 1) {
                if(endpoints[0] == -1) {
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
        if(endpoints[0] == -1) {
            isCyclic = true;
        }
    }

    public void extendP(boolean earlyStop) {
        for(int i = 0; i < 2; i++) {
            if(nextnodes[i] == -1) continue;
            while(true) {
                int step = -1;
                int pConnect = 0;
                int stepConnect = 0;
                for(int j = 0; j < graph.adj[nextnodes[i]].length; j++) {
                    if(j+1 < graph.adj[nextnodes[i]].length && graph.adj[nextnodes[i]][j] == graph.adj[nextnodes[i]][j+1]) continue;
                    if(setContained(graph.adj[nextnodes[i]][j], 'C')) continue;
                    if(setContained(graph.adj[nextnodes[i]][j], 'P')) {
                        pConnect++;
                        continue;
                    }
                    if(j == 0 || graph.adj[nextnodes[i]][j-1] != graph.adj[nextnodes[i]][j]) {
                        step = graph.adj[nextnodes[i]][j];
                    } else {
                        stepConnect++;
                    }
                    stepConnect++;
                }
                if((pConnect == 2 && stepConnect > 0) || stepConnect > 1 || step == nextnodes[i]) break;
                if(pConnect == 2) {
                    addP(nextnodes[i]);
                    endpoints[0] = -1;
                    endpoints[1] = -1;
                    nextnodes[0] = -1;
                    nextnodes[1] = -1;
                    isCyclic = true;
                    break;
                }
                addP(nextnodes[i]);
                endpoints[i] = nextnodes[i];
                nextnodes[i] = step;
                if(stepConnect == 0) break;
                if(earlyStop && P.length > C.length*(2*C.length + 1)) break;
            }
        }
    }

    public void computeMaxA() {
        computeMaxA(false);
    }

    public void computeMaxA(boolean onlyLengthCheck) {
        int[] allNodes = new int[C.length+P.length];
        System.arraycopy(C, 0, allNodes, 0, C.length);
        System.arraycopy(P, 0, allNodes, C.length, P.length);
        Graph pathAntlerGraph = GraphAlgorithm.subGraph(allNodes, graph);
        A = new int[0];
        for(int i = 0; i < C.length; i++) {
            if(GraphAlgorithm.hasFlower(P, C[i], graph) >= C.length+1) {
                addA(C[i]);
            } else if(!onlyLengthCheck && GraphAlgorithm.smartDisjointFVS(i, C.length, pathAntlerGraph) == null) {
                addA(C[i]);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof PathAntler)) return false;
        PathAntler otherPA = (PathAntler)other;
        if(otherPA.getA().length != A.length || otherPA.getC().length != C.length || otherPA.getP().length != P.length) return false;
        for(int i = 0; i < A.length; i++) if(otherPA.getA()[i] != A[i]) return false;
        for(int i = 0; i < C.length; i++) if(otherPA.getC()[i] != C[i]) return false;
        for(int i = 0; i < P.length; i++) if(otherPA.getP()[i] != P[i]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for(int i : A) {
            result = prime * result + i;
        }
        for(int i : C) {
            result = prime * result + i;
        }
        for(int i : P) {
            result = prime * result + i;
        }
        return result;
    }

    @Override
    public String toString() {
        return "A: "+Arrays.toString(A)+", C: "+Arrays.toString(C)+", P: "+Arrays.toString(P);
    }
}