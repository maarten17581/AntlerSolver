package thesis.antlersolver.algorithm;

import org.junit.jupiter.api.Test;

import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphAlgorithmTest {

    public Graph makeTestGraph() {
        Graph graph = new Graph("test");
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);
        graph.addNode(7);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(1, 5);
        graph.addEdge(1, 6);
        graph.addEdge(2, 2);
        graph.addEdge(2, 3);
        graph.addEdge(2, 5);
        graph.addEdge(2, 5);
        graph.addEdge(3,4, 2);
        graph.addEdge(4, 5);
        graph.addEdge(4,5, 2);
        return graph;
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
    public void testConnectedComponents() {
        Graph graph = makeTestGraph();
        List<List<Node>> cc = GraphAlgorithm.connectedComponents(graph);
        assertEquals(2, cc.size(), "Connected Components test 1 failed");
        assertEquals(7, cc.get(0).size(), "Connected Components test 2 failed");
        assertEquals(1, cc.get(1).size(), "Connected Components test 3 failed");
        assertTrue(cc.get(1).contains(graph.nodes.get(7)), "Connected Components test 4 failed");
        RemoveNodeCommand remove1 = new RemoveNodeCommand(2, graph);
        remove1.execute();
        RemoveNodeCommand remove2 = new RemoveNodeCommand(5, graph);
        remove2.execute();
        cc = GraphAlgorithm.connectedComponents(graph);
        assertEquals(3, cc.size(), "Connected Components test 5 failed");
        assertEquals(3, cc.get(0).size(), "Connected Components test 6 failed");
        assertEquals(2, cc.get(1).size(), "Connected Components test 7 failed");
        assertEquals(1, cc.get(2).size(), "Connected Components test 8 failed");
    }

    @Test
    public void testIsAcyclic() {
        Graph graph = makeTestGraph();
        assertFalse(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 1 failed");
        RemoveNodeCommand remove1 = new RemoveNodeCommand(2, graph);
        remove1.execute();
        RemoveNodeCommand remove2 = new RemoveNodeCommand(5, graph);
        remove2.execute();
        assertFalse(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 2 failed");
        RemoveNodeCommand remove3 = new RemoveNodeCommand(3, graph);
        remove3.execute();
        assertTrue(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 3 failed");
        graph.addEdge(0, 0);
        assertFalse(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 4 failed");
    }

    @Test
    public void testEdgeBCC() {
        Graph graph = makeTestGraph();
        List<Edge> bridges = GraphAlgorithm.edgeBCC(graph);
        assertEquals(1, bridges.size(), "Edge BCC test 1 failed");
        assertTrue(bridges.contains(graph.nodes.get(1).neighbors.get(graph.nodes.get(6))), "Edge BCC test 2 failed");
        RemoveEdgeCommand remove = new RemoveEdgeCommand(4, 5, 3, graph);
        remove.execute();
        AddEdgeCommand add = new AddEdgeCommand(1, 6, graph);
        add.execute();
        bridges = GraphAlgorithm.edgeBCC(graph);
        assertEquals(1, bridges.size(), "Edge BCC test 3 failed");
        assertTrue(bridges.contains(graph.nodes.get(2).neighbors.get(graph.nodes.get(3))), "Edge BCC test 4 failed");
    }

    @Test
    public void testSingletonPathAntlers() {
        Graph graph = makeTestGraph();
        List<PathAntler> pathAntlers = GraphAlgorithm.getSingletonPathAntlers(graph);
        assertEquals(4, pathAntlers.size(), "Singleton Path Antler test 1 failed");
        assertTrue(pathAntlers.get(0).getC().contains(graph.nodes.get(1)), "Singleton Path Antler test 2 failed");
        assertEquals(1, pathAntlers.get(0).getP().size(), "Singleton Path Antler test 3 failed");
        assertTrue(pathAntlers.get(1).getP().contains(graph.nodes.get(0)) || pathAntlers.get(1).getP().contains(graph.nodes.get(6)), "Singleton Path Antler test 4 failed");
        assertTrue(pathAntlers.get(1).getC().contains(graph.nodes.get(1)), "Singleton Path Antler test 5 failed");
        assertEquals(1, pathAntlers.get(1).getP().size(), "Singleton Path Antler test 6 failed");
        assertTrue(pathAntlers.get(1).getP().contains(graph.nodes.get(0)) || pathAntlers.get(1).getP().contains(graph.nodes.get(6)), "Singleton Path Antler test 7 failed");
        assertTrue(pathAntlers.get(2).getC().contains(graph.nodes.get(2)), "Singleton Path Antler test 8 failed");
        assertEquals(1, pathAntlers.get(2).getP().size(), "Singleton Path Antler test 9 failed");
        assertTrue(pathAntlers.get(2).getP().contains(graph.nodes.get(0)), "Singleton Path Antler test 10 failed");
        assertTrue(pathAntlers.get(3).getC().contains(graph.nodes.get(4)), "Singleton Path Antler test 11 failed");
        assertEquals(1, pathAntlers.get(3).getP().size(), "Singleton Path Antler test 12 failed");
        assertTrue(pathAntlers.get(3).getP().contains(graph.nodes.get(3)), "Singleton Path Antler test 13 failed");
    }

    @Test
    public void testKPathAntlers() {
        Graph graph = makeTestGraph();
        graph.addEdge(0, 6);
        graph.addEdge(5, 6);
        graph.removeEdge(2, 2);
        assertEquals(6, GraphAlgorithm.getKPathAntlers(1, graph, false).size(), "K Path Antler test 1 failed");
        assertEquals(18, GraphAlgorithm.getKPathAntlers(2, graph, false).size(), "K Path Antler test 2 failed");
        assertEquals(16, GraphAlgorithm.getKPathAntlers(3, graph, false).size(), "K Path Antler test 3 failed");
        assertEquals(8, GraphAlgorithm.getKPathAntlers(4, graph, false).size(), "K Path Antler test 4 failed");
        assertEquals(0, GraphAlgorithm.getKPathAntlers(5, graph, false).size(), "K Path Antler test 5 failed");
    }

    @Test
    public void testKAntlers() {
        Graph graph = makeTestGraph();
        graph.addEdge(0, 6);
        graph.addEdge(5, 6);
        graph.addEdge(3, 5);
        graph.removeEdge(2, 2);
        assertEquals(3, GraphAlgorithm.findKAntlers(2, graph).size(), "K Antler test 1 failed");
        assertEquals(6, GraphAlgorithm.findKAntlers(3, graph).size(), "K Antler test 2 failed");
        assertEquals(0, GraphAlgorithm.findKAntlers(4, graph).size(), "K Antler test 3 failed");
    }

    @Test
    public void testNaiveFVS() {
        Graph graph = makeTestGraph();
        List<Node> fvs = GraphAlgorithm.naiveFVS(graph);
        assertEquals(2, fvs.size(), "Naive FVS test 1 failed");
        assertTrue(fvs.contains(graph.nodes.get(2)), "Naive FVS test 2 failed");
        assertTrue(fvs.contains(graph.nodes.get(4)), "Naive FVS test 3 failed");
    }

    @Test
    public void testNaiveDisjointFVS() {
        Graph graph = makeTestGraph();
        List<Node> fvs = GraphAlgorithm.naiveDisjointFVS(graph.nodes.get(4), graph);
        assertEquals(3, fvs.size(), "Naive Disjoint FVS test 1 failed");
        assertTrue(fvs.contains(graph.nodes.get(2)), "Naive Disjoint FVS test 2 failed");
        assertTrue(fvs.contains(graph.nodes.get(3)), "Naive Disjoint FVS test 3 failed");
        assertTrue(fvs.contains(graph.nodes.get(5)), "Naive Disjoint FVS test 4 failed");
        assertEquals(null, GraphAlgorithm.naiveDisjointFVS(graph.nodes.get(4), 2, graph), "Naive Disjoint FVS test 5 failed");
        assertEquals(null, GraphAlgorithm.naiveDisjointFVS(graph.nodes.get(2), graph), "Naive Disjoint FVS test 6 failed");
    }

    @Test
    public void testSmartFVS() {
        Graph graph = makeTestGraph();
        List<Node> fvs = GraphAlgorithm.smartFVS(graph);
        assertEquals(2, fvs.size(), "Smart FVS test 1 failed");
        assertTrue(fvs.contains(graph.nodes.get(2)), "Smart FVS test 2 failed");
        assertTrue(fvs.contains(graph.nodes.get(4)), "Smart FVS test 3 failed");
    }

    @Test
    public void testSmartDisjointFVS() {
        Graph graph = makeTestGraph();
        List<Node> fvs = GraphAlgorithm.smartDisjointFVS(graph.nodes.get(4), 3, graph);
        assertEquals(3, fvs.size(), "Smart Disjoint FVS test 1 failed");
        assertTrue(fvs.contains(graph.nodes.get(2)), "Smart Disjoint FVS test 2 failed");
        assertTrue(fvs.contains(graph.nodes.get(3)), "Smart Disjoint FVS test 3 failed");
        assertTrue(fvs.contains(graph.nodes.get(5)), "Smart Disjoint FVS test 4 failed");
    }
}
