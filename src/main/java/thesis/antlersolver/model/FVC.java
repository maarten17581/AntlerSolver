package thesis.antlersolver.model;

import java.util.Arrays;

import fvs_wata_orz.Graph;
import fvs_wata_orz.tc.wata.util.Utils;
import thesis.antlersolver.algorithm.GraphAlgorithm;

public class FVC {
    private int[] A;
    private int[] C;
    private int[] F;
    public int aCount;
    public Graph graph;

    public int[] getA() {
        return A;
    }

    public int[] getC() {
        return C;
    }

    public int[] getF() {
        return F;
    }

    private void add(int v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'F') {
            S = F;
        }
        int i = Arrays.binarySearch(S, v);
        if(i >= 0) return;
        i = -i-1;
		int[] a = new int[S.length + 1];
		System.arraycopy(S, 0, a, 0, i);
		a[i] = v;
		System.arraycopy(S, i, a, i + 1, S.length - i);
        if(set == 'A') {
            A = a;
        } else if(set == 'C') {
            C = a;
        } else if(set == 'F') {
            F = a;
        }
    }

    private void remove(int v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'F') {
            S = F;
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
        } else if(set == 'F') {
            F = a;
        }
    }

    public void addA(int v) {
        add(v, 'A');
    }

    public void addC(int v) {
        add(v, 'C');
    }

    public void addF(int v) {
        add(v, 'F');
    }

    public void removeA(int v) {
        remove(v, 'A');
    }

    public void removeC(int v) {
        remove(v, 'C');
    }

    public void removeF(int v) {
        remove(v, 'F');
    }

    public boolean inA(int v) {
        return setContained(v, 'A');
    }

    public boolean inC(int v) {
        return setContained(v, 'C');
    }

    public boolean inF(int v) {
        return setContained(v, 'F');
    }

    private boolean setContained(int v, char set) {
        int[] S = new int[0];
        if(set == 'A') {
            S = A;
        } else if(set == 'C') {
            S = C;
        } else if(set == 'F') {
            S = F;
        }
        return Arrays.binarySearch(S, v) >= 0;
    }

    public FVC(Graph graph) {
        this.graph = graph;
        A = new int[0];
        C = new int[0];
        F = new int[0];
        aCount = 0;
    }

    public FVC(Graph graph, int[] C) {
        this(graph);
        for(int v : C) {
            addC(v);
        }
        setMaxF();
    }

    public void setMaxF() {
        F = new int[0];
        for(int v : GraphAlgorithm.getF(C, graph)) {
            addF(v);
        }
    }

    public void computeMaxA() {
        computeMaxA(false);
    }

    public void computeMaxA(boolean onlyFlower) {
        int[] allNodes = new int[C.length+F.length];
        System.arraycopy(C, 0, allNodes, 0, C.length);
        System.arraycopy(F, 0, allNodes, C.length, F.length);
        Graph antlerGraph = GraphAlgorithm.subGraph(allNodes, graph);
        A = new int[0];
        aCount = 0;
        for(int i = 0; i < C.length; i++) {
            if(GraphAlgorithm.hasFlower(F, C[i], graph) >= C.length) {
                addA(C[i]);
            } else if(!onlyFlower) {
                int[] fvs = GraphAlgorithm.smartDisjointFVS(i, C.length-1, antlerGraph);
                if(fvs == null) addA(C[i]);
                else aCount += fvs.length;
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof FVC)) return false;
        FVC otherFVC = (FVC)other;
        if(otherFVC.getA().length != A.length || otherFVC.getC().length != C.length || otherFVC.getF().length != F.length) return false;
        for(int i = 0; i < A.length; i++) if(otherFVC.getA()[i] != A[i]) return false;
        for(int i = 0; i < C.length; i++) if(otherFVC.getC()[i] != C[i]) return false;
        for(int i = 0; i < F.length; i++) if(otherFVC.getF()[i] != F[i]) return false;
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
        for(int i : F) {
            result = prime * result + i;
        }
        return result;
    }

    @Override
    public String toString() {
        return "A: "+Arrays.toString(A)+", C: "+Arrays.toString(C)+", F: "+Arrays.toString(F);
    }
}
