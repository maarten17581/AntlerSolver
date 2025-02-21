package thesis.antlersolver.algorithm;

import org.junit.jupiter.api.Test;

import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.PathAntler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
        RemoveNodeCommand remove2 = new RemoveNodeCommand(3, graph);
        remove2.execute();
        RemoveNodeCommand remove3 = new RemoveNodeCommand(5, graph);
        remove3.execute();
        assertTrue(GraphAlgorithm.isAcyclic(graph), "Is Acyclic test 2 failed");
    }

    @Test
    public void testEdgeBCC() {
        Graph graph = makeTestGraph();
        List<Edge> bridges = GraphAlgorithm.EdgeBCC(graph);
        assertEquals(1, bridges.size(), "Edge BCC test 1 failed");
        assertTrue(bridges.contains(graph.nodes.get(1).neighbors.get(graph.nodes.get(6))), "Edge BCC test 2 failed");
        RemoveEdgeCommand remove = new RemoveEdgeCommand(4, 5, 3, graph);
        remove.execute();
        AddEdgeCommand add = new AddEdgeCommand(1, 6, graph);
        add.execute();
        bridges = GraphAlgorithm.EdgeBCC(graph);
        assertEquals(1, bridges.size(), "Edge BCC test 3 failed");
        assertTrue(bridges.contains(graph.nodes.get(2).neighbors.get(graph.nodes.get(3))), "Edge BCC test 4 failed");
    }

    @Test
    public void testSingletonPathAntlers() {
        Graph graph = makeTestGraph();
        List<PathAntler> pathAntlers = GraphAlgorithm.getSingletonPathAntlers(graph);
        assertEquals(4, pathAntlers.size(), "Singleton Path Antler test 1 failed");
        assertTrue(pathAntlers.get(0).C.contains(graph.nodes.get(1)), "Singleton Path Antler test 2 failed");
        assertEquals(1, pathAntlers.get(0).P.size(), "Singleton Path Antler test 3 failed");
        assertTrue(pathAntlers.get(1).P.contains(graph.nodes.get(0)) || pathAntlers.get(1).P.contains(graph.nodes.get(6)), "Singleton Path Antler test 4 failed");
        assertTrue(pathAntlers.get(1).C.contains(graph.nodes.get(1)), "Singleton Path Antler test 5 failed");
        assertEquals(1, pathAntlers.get(1).P.size(), "Singleton Path Antler test 6 failed");
        assertTrue(pathAntlers.get(1).P.contains(graph.nodes.get(0)) || pathAntlers.get(1).P.contains(graph.nodes.get(6)), "Singleton Path Antler test 7 failed");
        assertTrue(pathAntlers.get(2).C.contains(graph.nodes.get(2)), "Singleton Path Antler test 8 failed");
        assertEquals(1, pathAntlers.get(2).P.size(), "Singleton Path Antler test 9 failed");
        assertTrue(pathAntlers.get(2).P.contains(graph.nodes.get(0)), "Singleton Path Antler test 10 failed");
        assertTrue(pathAntlers.get(3).C.contains(graph.nodes.get(4)), "Singleton Path Antler test 11 failed");
        assertEquals(1, pathAntlers.get(3).P.size(), "Singleton Path Antler test 12 failed");
        assertTrue(pathAntlers.get(3).P.contains(graph.nodes.get(3)), "Singleton Path Antler test 13 failed");
    }
}
