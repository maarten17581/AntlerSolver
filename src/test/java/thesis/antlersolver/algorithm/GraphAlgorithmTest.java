package thesis.antlersolver.algorithm;

import org.junit.jupiter.api.Test;

import fvs_wata_orz.Graph;
import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphAlgorithmTest {

    public Graph makeTestGraph() {
        int[][] adj = new int[][]{{1,2},{0,2,5,6},{0,1,2,3,5,5},{2,4,4},{3,3,5,5},{1,2,2,4,4},{1},{}};
        return new Graph(adj);
    }

    public Graph makeTestGraph2() {
        int[][] adj = new int[][]{{1,2,6},{0,2,5,6},{0,1,3,5,5},{2,4,4,5},{3,3,5,5},{1,2,2,3,4,4,6},{0,1,5},{}};
        return new Graph(adj);
    }

    public Pair<FVC, Graph> makeTestFVC() {
        Graph graph = new Graph("test");
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);
        graph.addNode(7);
        graph.addNode(8);
        graph.addNode(9);
        graph.addNode(10);
        graph.addNode(11);
        graph.addNode(12);
        graph.addEdge(0, 3);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(4, 7);
        graph.addEdge(7, 8);
        graph.addEdge(7, 9);
        graph.addEdge(10, 0);
        graph.addEdge(10, 1);
        graph.addEdge(11, 2);
        graph.addEdge(11, 4);
        graph.addEdge(12, 8);
        graph.addEdge(12, 9);
        graph.addEdge(10, 11, 2);
        Set<Node> C = new HashSet<>(Arrays.asList(new Node[]{graph.nodes.get(10), graph.nodes.get(11), graph.nodes.get(12)}));
        FVC fvc = new FVC(graph, C);
        return new Pair<FVC,Graph>(fvc, graph);
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
        assertEquals(18, GraphAlgorithm.getKPathAntlers(2, graph, false).length, "K Path Antler test 2 failed");
        assertEquals(19, GraphAlgorithm.getKPathAntlers(3, graph, false).length, "K Path Antler test 3 failed");
        assertEquals(8, GraphAlgorithm.getKPathAntlers(4, graph, false).length, "K Path Antler test 4 failed");
        assertEquals(1, GraphAlgorithm.getKPathAntlers(5, graph, false).length, "K Path Antler test 5 failed");
        assertEquals(0, GraphAlgorithm.getKPathAntlers(6, graph, false).length, "K Path Antler test 6 failed");
    }

    @Test
    public void testKAntlers() {
        Graph graph = makeTestGraph2();
        for(FVC fvc : GraphAlgorithm.findKAntlers(3, graph, false)) {
            System.out.println(fvc);
        }
        assertEquals(3, GraphAlgorithm.findKAntlers(2, graph, false).length, "K Antler test 1 failed");
        assertEquals(6, GraphAlgorithm.findKAntlers(3, graph, false).length, "K Antler test 2 failed");
        assertEquals(0, GraphAlgorithm.findKAntlers(4, graph, false).length, "K Antler test 3 failed");
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
}
