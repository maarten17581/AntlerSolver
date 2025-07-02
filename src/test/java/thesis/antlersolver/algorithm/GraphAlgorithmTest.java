package thesis.antlersolver.algorithm;

import org.junit.jupiter.api.Test;

import fvs_wata_orz.Graph;
import thesis.antlersolver.model.Description;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.PathAntler;

import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmTest {

    public Graph makeTestGraph() {
        int[][] adj = new int[][]{{1,2},{0,2,5,6},{0,1,2,3,5,5},{2,4,4},{3,3,5,5},{1,2,2,4,4},{1},{}};
        return new Graph(adj);
    }

    public Graph makeTestGraph2() {
        int[][] adj = new int[][]{{1,2,6},{0,2,5,6},{0,1,3,5,5},{2,4,4,5},{3,3,5,5},{1,2,2,3,4,4,6},{0,1,5},{}};
        return new Graph(adj);
    }

    @Test
    public void testIsAcyclic() {
        Graph graph = makeTestGraph();
        assertFalse(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 1 failed");
        graph.removeV(3);
        graph.removeV(5);
        assertFalse(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 2 failed");
        graph.removeV(2);
        assertTrue(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 3 failed");
    }

    @Test
    public void testSubGraph() {
        Graph graph = makeTestGraph();
        int[] subset = new int[]{0,2,1};
        graph = GraphAlgorithm.subGraph(subset, graph);
        assertEquals(3, graph.n, "Sub Graph test 1 failed");
        assertEquals(2, graph.adj[0].length, "Sub Graph test 2 failed");
        assertEquals(3, graph.adj[1].length, "Sub Graph test 3 failed");
        assertEquals(2, graph.adj[2].length, "Sub Graph test 4 failed");
    }

    @Test
    public void testSingletonPathAntlers() {
        Graph graph = makeTestGraph();
        PathAntler[] pathAntlers = GraphAlgorithm.getSingletonPathAntlers(graph);
        assertEquals(4, pathAntlers.length, "Singleton Path Antler test 1 failed");
        assertEquals(0, pathAntlers[0].getA().length, "Singleton Path Antler test 2 failed");
        assertEquals(1, pathAntlers[0].getC().length, "Singleton Path Antler test 3 failed");
        assertEquals(1, pathAntlers[0].getC()[0], "Singleton Path Antler test 4 failed");
        assertEquals(1, pathAntlers[0].getP().length, "Singleton Path Antler test 5 failed");
        assertEquals(0, pathAntlers[0].getP()[0], "Singleton Path Antler test 6 failed");
        assertEquals(0, pathAntlers[1].getA().length, "Singleton Path Antler test 7 failed");
        assertEquals(1, pathAntlers[1].getC().length, "Singleton Path Antler test 8 failed");
        assertEquals(1, pathAntlers[1].getC()[0], "Singleton Path Antler test 9 failed");
        assertEquals(1, pathAntlers[1].getP().length, "Singleton Path Antler test 10 failed");
        assertEquals(6, pathAntlers[1].getP()[0], "Singleton Path Antler test 11 failed");
        assertEquals(1, pathAntlers[2].getA().length, "Singleton Path Antler test 12 failed");
        assertEquals(2, pathAntlers[2].getA()[0], "Singleton Path Antler test 13 failed");
        assertEquals(1, pathAntlers[2].getC().length, "Singleton Path Antler test 14 failed");
        assertEquals(2, pathAntlers[2].getC()[0], "Singleton Path Antler test 15 failed");
        assertEquals(1, pathAntlers[2].getP().length, "Singleton Path Antler test 16 failed");
        assertEquals(0, pathAntlers[2].getP()[0], "Singleton Path Antler test 17 failed");
        assertEquals(0, pathAntlers[3].getA().length, "Singleton Path Antler test 18 failed");
        assertEquals(1, pathAntlers[3].getC().length, "Singleton Path Antler test 19 failed");
        assertEquals(4, pathAntlers[3].getC()[0], "Singleton Path Antler test 20 failed");
        assertEquals(1, pathAntlers[3].getP().length, "Singleton Path Antler test 21 failed");
        assertEquals(3, pathAntlers[3].getP()[0], "Singleton Path Antler test 22 failed");
    }

    @Test
    public void testKPathAntlers() {
        Graph graph = makeTestGraph2();
        assertEquals(6, GraphAlgorithm.getKPathAntlers(1, graph, false).length, "K Path Antler test 1 failed");
        assertEquals(3, GraphAlgorithm.getKPathAntlers(2, graph, false).length, "K Path Antler test 2 failed");
        assertEquals(5, GraphAlgorithm.getKPathAntlers(1, graph, false, 5).length, "K Path Antler test 3 failed");
    }

    @Test
    public void testKAntlers() {
        Graph graph = makeTestGraph2();
        assertEquals(3, GraphAlgorithm.getKAntlers(2, graph, false).length, "K Antler test 1 failed");
        assertEquals(6, GraphAlgorithm.getKAntlers(3, graph, false).length, "K Antler test 2 failed");
        assertEquals(0, GraphAlgorithm.getKAntlers(4, graph, false).length, "K Antler test 3 failed");
    }

    @Test
    public void testKAntlersHeuristic() {
        Graph graph = makeTestGraph2();
        FVC fvc = GraphAlgorithm.getKAntlerHeuristicF(3, graph, false, false);
        assertEquals(3, fvc.getC().length, "K Antler Heuristic test 1 failed");
        fvc = GraphAlgorithm.getKAntlerHeuristicFlower(3, graph, false, false);
        assertEquals(3, fvc.getC().length, "K Antler Heuristic test 2 failed");
        fvc = GraphAlgorithm.getKAntlerHeuristicEdge(3, graph, false, false);
        assertEquals(3, fvc.getC().length, "K Antler Heuristic test 3 failed");
        fvc = GraphAlgorithm.getKAntlerHeuristicDiameter(3, graph, false, false);
        assertEquals(3, fvc.getC().length, "K Antler Heuristic test 4 failed");
    }

    @Test
    public void testNaiveFVS() {
        Graph graph = makeTestGraph();
        int[] fvs = GraphAlgorithm.naiveFVS(graph);
        assertEquals(2, fvs.length, "Naive FVS test 1 failed");
        assertEquals(2, fvs[0], "Naive FVS test 2 failed");
        assertEquals(4, fvs[1], "Naive FVS test 3 failed");
        assertEquals(null, GraphAlgorithm.naiveFVS(1, graph), "Naive FVS test 4 failed");
    }

    @Test
    public void testNaiveDisjointFVS() {
        Graph graph = makeTestGraph();
        int[] fvs = GraphAlgorithm.naiveDisjointFVS(4, graph);
        assertEquals(3, fvs.length, "Naive Disjoint FVS test 1 failed");
        assertEquals(2, fvs[0], "Naive Disjoint FVS test 2 failed");
        assertEquals(3, fvs[1], "Naive Disjoint FVS test 3 failed");
        assertEquals(5, fvs[2], "Naive Disjoint FVS test 4 failed");
        assertEquals(null, GraphAlgorithm.naiveDisjointFVS(4, 2, graph), "Naive Disjoint FVS test 5 failed");
        assertEquals(null, GraphAlgorithm.naiveDisjointFVS(2, graph), "Naive Disjoint FVS test 6 failed");
    }

    @Test
    public void testSmartFVS() {
        Graph graph = makeTestGraph();
        int[] fvs = GraphAlgorithm.smartFVS(graph);
        assertEquals(2, fvs.length, "Smart FVS test 1 failed");
        assertEquals(2, fvs[0], "Smart FVS test 2 failed");
        assertEquals(4, fvs[1], "Smart FVS test 3 failed");
        assertEquals(null, GraphAlgorithm.smartFVS(1, graph), "Smart FVS test 4 failed");
    }

    @Test
    public void testSmartDisjointFVS() {
        Graph graph = makeTestGraph();
        int[] fvs = GraphAlgorithm.smartDisjointFVS(4, graph);
        assertEquals(3, fvs.length, "Smart Disjoint FVS test 1 failed");
        assertEquals(2, fvs[0], "Smart Disjoint FVS test 2 failed");
        assertEquals(3, fvs[1], "Smart Disjoint FVS test 3 failed");
        assertEquals(5, fvs[2], "Smart Disjoint FVS test 4 failed");
        assertEquals(null, GraphAlgorithm.smartDisjointFVS(4, 2, graph), "Smart Disjoint FVS test 5 failed");
        assertEquals(null, GraphAlgorithm.smartDisjointFVS(2, graph), "Smart Disjoint FVS test 6 failed");
    }

    @Test
    public void testHeuristicFVS() {
        Graph graph = makeTestGraph();
        int[] fvs = GraphAlgorithm.heuristicFVS(graph);
        assertTrue(2 <= fvs.length, "Heuristic FVS test 1 failed");
    }

    @Test
    public void testKSecludedTrees() {
        Graph graph = makeTestGraph2();
        Description[] descriptions = GraphAlgorithm.getKSecludedTrees(3, graph);
        assertEquals(8, descriptions.length, "K Secluded Trees test 1 failed");
    }

    @Test
    public void testSingleTreeAntlers() {
        Graph graph = makeTestGraph2();
        FVC[] fvcs = GraphAlgorithm.getSingleTreeAntlers(3, graph);
        assertEquals(4, fvcs.length, "Single Tree Antler test 1 failed");
    }

    @Test
    public void testConnectGraphs() {
        Graph graph = GraphAlgorithm.connectGraphs(new Graph[]{makeTestGraph(), makeTestGraph2()}, new int[]{0}, new int[]{8});
        assertEquals(16, graph.n, "Connect Graphs test 1 failed");
        assertEquals(14, graph.n(), "Connect Graphs test 2 failed");
        assertEquals(28, graph.m(), "Connect Graphs test 3 failed");
    }

    @Test
    public void testRandomGraph() {
        int n = 100;
        double[] p = new double[]{0.1, 0.5, 0.9};
        for(int i = 0; i < p.length; i++) {
            Graph g = GraphAlgorithm.randomGraph(n, p[i]);
            int trial = n*(n-1)/2;
            // assertTrue(p[i]*trial-3*Math.sqrt(trial*p[i]*(1-p[i])) < g.m() && g.m() < p[i]*trial+3*Math.sqrt(trial*p[i]*(1-p[i])), "Random Graph test "+(i+1)+" failed");
        }
    }

    @Test
    public void testRandomTree() {
        int n = 100;
        Graph t = GraphAlgorithm.randomTree(n);
        // assertEquals(n-1, t.m(), "Random Tree test 1 failed");
    }

    @Test
    public void testRandomForest() {
        int n = 100;
        int c = 10;
        Graph f1 = GraphAlgorithm.randomForest(n, c);
        // assertEquals(n-c, f1.m(), "Random Forest test 1 failed");
        double p = 0.9;
        Graph f2 = GraphAlgorithm.randomForest(n, p);
        // assertTrue(1+Math.log(1-0.005)/Math.log(p) < n-f2.m() && n-f2.m() < 1+Math.log(1-0.995)/Math.log(p), "Random Forest test 1 failed");
    }

    @Test
    public void testRandomAntlerGraph() {
        int f = 45;
        int k = 5;
        int n = 50;
        int m = 5;
        double c = 0.9;
        double fk = 0.9;
        double kk = 0.9;
        double p = 0.5;
        double t = 1;
        Graph g = GraphAlgorithm.randomAntlerGraph(f, k, n, m, c, fk, kk, p, t);
        int fEdges = 0;
        int fkEdges = 0;
        int kEdges = 0;
        int nkEdges = 0;
        int nEdges = 0;
        int fnEdges = 0;
        for(int i = 0; i < f+k+n; i++) {
            for(int j : g.adj[i]) {
                if(i < f && j < f) fEdges++;
                if(i < f && j >= f && j < f+k) fkEdges++;
                if(i < f && j >= f+k) fnEdges++;
                if(i >= f && i < f+k && j < f) fkEdges++;
                if(i >= f && i < f+k && j >= f && j < f+k) kEdges++;
                if(i >= f && i < f+k && j >= f+k) nkEdges++;
                if(i >= f+k && j < f) fnEdges++;
                if(i >= f+k && j >= f && j < f+k) nkEdges++;
                if(i >= f+k && j >= f+k) nEdges++;
            }
        }
        fEdges /= 2;
        fkEdges /= 2;
        kEdges /= 2;
        nkEdges /= 2;
        nEdges /= 2;
        fnEdges /= 2;
        int forestComp = f-fEdges;
        // assertTrue(1+Math.log(1-0.005)/Math.log(c) < forestComp && forestComp < 1+Math.log(1-0.995)/Math.log(c), "Random Antler Graph test 1 failed");
        // assertTrue(fk*f*k-3*Math.sqrt(f*k*fk*(1-fk)) < fkEdges && fkEdges < fk*f*k+3*Math.sqrt(f*k*fk*(1-fk)), "Random Antler Graph test 2 failed");
        // assertTrue(kk*k*(k-1)/2-3*Math.sqrt(k*(k-1)/2*kk*(1-kk)) < kEdges && kEdges < kk*k*(k-1)/2+3*Math.sqrt(k*(k-1)/2*kk*(1-kk)), "Random Antler Graph test 3 failed");
        // assertTrue(p*n*k-3*Math.sqrt(n*k*p*(1-p)) < nkEdges && nkEdges < p*n*k+3*Math.sqrt(n*k*p*(1-p)), "Random Antler Graph test 4 failed");
        // assertTrue(p*n*(n-1)/2-3*Math.sqrt(n*(n-1)/2*p*(1-p)) < nEdges && nEdges < p*n*(n-1)/2+3*Math.sqrt(n*(n-1)/2*p*(1-p)), "Random Antler Graph test 5 failed");
        // assertEquals(forestComp, fnEdges, "Random Antler Graph test 6 failed");
    }

    @Test
    public void testRandomAntlerGraphMinA() {
        int f = 45;
        int k = 5;
        int n = 50;
        int m = 5;
        double c = 0.9;
        int a = 5;
        double p = 0.5;
        double t = 1;
        Graph g = GraphAlgorithm.randomAntlerGraph(f, k, n, m, c, a, p, t);
        int[] antlernodes = new int[f+k];
        int[] head = new int[k];
        for(int i = 0; i < f+k; i++) antlernodes[i] = i;
        for(int i = 0; i < k; i++) head[i] = i+f;
        Graph subGraph = GraphAlgorithm.subGraph(antlernodes, g);
        FVC fvc = new FVC(subGraph, head);
        fvc.computeMaxA();
        assertTrue(a <= fvc.getA().length, "Random Antler Graph Min A test 1 failed");
    }

    @Test
    public void testRandomPathAntlerGraph() {
        int p = 45;
        int k = 5;
        int n = 50;
        int m = 5;
        double pk = 0.9;
        double kk = 0.9;
        double r = 0.5;
        double t = 1;
        Graph g = GraphAlgorithm.randomPathAntlerGraph(p, k, n, m, pk, kk, r, t);
        int pEdges = 0;
        int pkEdges = 0;
        int kEdges = 0;
        int nkEdges = 0;
        int nEdges = 0;
        int pnEdges = 0;
        for(int i = 0; i < p+k+n; i++) {
            for(int j : g.adj[i]) {
                if(i < p && j < p) pEdges++;
                if(i < p && j >= p && j < p+k) pkEdges++;
                if(i < p && j >= p+k) pnEdges++;
                if(i >= p && i < p+k && j < p) pkEdges++;
                if(i >= p && i < p+k && j >= p && j < p+k) kEdges++;
                if(i >= p && i < p+k && j >= p+k) nkEdges++;
                if(i >= p+k && j < p) pnEdges++;
                if(i >= p+k && j >= p && j < p+k) nkEdges++;
                if(i >= p+k && j >= p+k) nEdges++;
            }
        }
        pEdges /= 2;
        pkEdges /= 2;
        kEdges /= 2;
        nkEdges /= 2;
        nEdges /= 2;
        pnEdges /= 2;
        // assertEquals(p-1, pEdges, "Random Path Antler Graph test 1 failed");
        // assertTrue(pk*p*k-3*Math.sqrt(p*k*pk*(1-pk)) < pkEdges && pkEdges < pk*p*k+3*Math.sqrt(p*k*pk*(1-pk)), "Random Path Antler Graph test 2 failed");
        // assertTrue(kk*k*(k-1)/2-3*Math.sqrt(k*(k-1)/2*kk*(1-kk)) < kEdges && kEdges < kk*k*(k-1)/2+3*Math.sqrt(k*(k-1)/2*kk*(1-kk)), "Random Path Antler Graph test 3 failed");
        // assertTrue(r*n*k-3*Math.sqrt(n*k*r*(1-r)) < nkEdges && nkEdges < r*n*k+3*Math.sqrt(n*k*r*(1-r)), "Random Path Antler Graph test 4 failed");
        // assertTrue(r*n*(n-1)/2-3*Math.sqrt(n*(n-1)/2*r*(1-r)) < nEdges && nEdges < r*n*(n-1)/2+3*Math.sqrt(n*(n-1)/2*r*(1-r)), "Random Path Antler Graph test 5 failed");
        // assertEquals(2, pnEdges, "Random Path Antler Graph test 6 failed");
    }

    @Test
    public void testRandomPathAntlerGraphMinA() {
        int p = 45;
        int k = 5;
        int n = 50;
        int m = 5;
        int a = 5;
        double r = 0.5;
        double t = 1;
        Graph g = GraphAlgorithm.randomPathAntlerGraph(p, k, n, m, a, r, t);
        int[] pathantlernodes = new int[p+k];
        int[] path = new int[p];
        int[] head = new int[k];
        for(int i = 0; i < p+k; i++) pathantlernodes[i] = i;
        for(int i = 0; i < p; i++) path[i] = i;
        for(int i = 0; i < k; i++) head[i] = i+p;
        Graph subGraph = GraphAlgorithm.subGraph(pathantlernodes, g);
        PathAntler pa = new PathAntler(subGraph, new int[0], head, path);
        pa.computeStatistics();
        pa.computeMaxA();
        assertTrue(a <= pa.getA().length, "Random Path Antler Graph Min A test 1 failed");
    }

    @Test
    public void testRandomSingleTreeAntlerGraph() {
        int f = 45;
        int k = 5;
        int n = 50;
        int m = 5;
        double fk = 0.9;
        double kk = 0.9;
        double p = 0.5;
        double t = 1;
        Graph g = GraphAlgorithm.randomSingleTreeAntlerGraph(f, k, n, m, fk, kk, p, t);
        int fEdges = 0;
        int fkEdges = 0;
        int kEdges = 0;
        int nkEdges = 0;
        int nEdges = 0;
        int fnEdges = 0;
        for(int i = 0; i < f+k+n; i++) {
            for(int j : g.adj[i]) {
                if(i < f && j < f) fEdges++;
                if(i < f && j >= f && j < f+k) fkEdges++;
                if(i < f && j >= f+k) fnEdges++;
                if(i >= f && i < f+k && j < f) fkEdges++;
                if(i >= f && i < f+k && j >= f && j < f+k) kEdges++;
                if(i >= f && i < f+k && j >= f+k) nkEdges++;
                if(i >= f+k && j < f) fnEdges++;
                if(i >= f+k && j >= f && j < f+k) nkEdges++;
                if(i >= f+k && j >= f+k) nEdges++;
            }
        }
        fEdges /= 2;
        fkEdges /= 2;
        kEdges /= 2;
        nkEdges /= 2;
        nEdges /= 2;
        fnEdges /= 2;
        // assertEquals(f-1, fEdges, "Random Single Tree Antler Graph test 1 failed");
        // assertTrue(fk*f*k-3*Math.sqrt(f*k*fk*(1-fk)) < fkEdges && fkEdges < fk*f*k+3*Math.sqrt(f*k*fk*(1-fk)), "Random Single Tree Antler Graph test 2 failed");
        // assertTrue(kk*k*(k-1)/2-3*Math.sqrt(k*(k-1)/2*kk*(1-kk)) < kEdges && kEdges < kk*k*(k-1)/2+3*Math.sqrt(k*(k-1)/2*kk*(1-kk)), "Random Single Tree Antler Graph test 3 failed");
        // assertTrue(p*n*k-3*Math.sqrt(n*k*p*(1-p)) < nkEdges && nkEdges < p*n*k+3*Math.sqrt(n*k*p*(1-p)), "Random Single Tree Antler Graph test 4 failed");
        // assertTrue(p*n*(n-1)/2-3*Math.sqrt(n*(n-1)/2*p*(1-p)) < nEdges && nEdges < p*n*(n-1)/2+3*Math.sqrt(n*(n-1)/2*p*(1-p)), "Random Single Tree Antler Graph test 5 failed");
        // assertEquals(1, fnEdges, "Random Single Tree Antler Graph test 6 failed");
    }
}
