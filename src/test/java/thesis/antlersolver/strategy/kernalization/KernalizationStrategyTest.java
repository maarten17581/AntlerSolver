package thesis.antlersolver.strategy.kernalization;

import org.junit.jupiter.api.Test;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class KernalizationStrategyTest {

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
    public void testIsolatedStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new IsolatedStrategy();
        assertEquals(8, graph.nodecount, "Isolated Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(7), "Isolated Strategy test 2 failed");
        Command command = strategy.apply(graph).a;
        assertEquals(7, graph.nodecount, "Isolated Strategy test 3 failed");
        assertEquals(null, graph.nodes.get(7), "Isolated Strategy test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Isolated Strategy test 5 failed");
        assertNotEquals(null, graph.nodes.get(7), "Isolated Strategy test 6 failed");
    }

    @Test
    public void testLeafStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new LeafStrategy();
        assertEquals(8, graph.nodecount, "Leaf Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(6), "Leaf Strategy test 2 failed");
        Command command = strategy.apply(graph).a;
        assertEquals(7, graph.nodecount, "Leaf Strategy test 3 failed");
        assertEquals(null, graph.nodes.get(6), "Leaf Strategy test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Leaf Strategy test 5 failed");
        assertNotEquals(null, graph.nodes.get(6), "Leaf Strategy test 6 failed");
    }

    @Test
    public void testDegree2Strategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new Degree2Strategy();
        assertEquals(8, graph.nodecount, "Degree 2 Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(0), "Degree 2 Strategy test 2 failed");
        Command command = strategy.apply(graph).a;
        assertEquals(7, graph.nodecount, "Degree 2 Strategy test 3 failed");
        assertEquals(null, graph.nodes.get(0), "Degree 2 Strategy test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Degree 2 Strategy test 5 failed");
        assertNotEquals(null, graph.nodes.get(0), "Degree 2 Strategy test 6 failed");
    }

    @Test
    public void testMultiEdgeStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new MultiEdgeStrategy();
        assertEquals(14, graph.edgecount, "Multi Edge Strategy test 1 failed");
        assertEquals(3, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Multi Edge Strategy test 2 failed");
        Command command = strategy.apply(graph).a;
        assertEquals(13, graph.edgecount, "Multi Edge Strategy test 3 failed");
        assertEquals(2, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Multi Edge Strategy test 4 failed");
        command.undo();
        assertEquals(14, graph.edgecount, "Multi Edge Strategy test 5 failed");
        assertEquals(3, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Multi Edge Strategy test 6 failed");
    }

    @Test
    public void testSelfloopStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new SelfloopStrategy();
        Node v = graph.nodes.get(2);
        assertEquals(8, graph.nodecount, "Selfloop Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(2), "Selfloop Strategy test 2 failed");
        Pair<Command, List<Node>> pair = strategy.apply(graph);
        Command command = pair.a;
        List<Node> solutionSet = pair.b;
        assertEquals(7, graph.nodecount, "Selfloop Strategy test 3 failed");
        assertEquals(null, graph.nodes.get(2), "Selfloop Strategy test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Selfloop Strategy test 5 failed");
        assertNotEquals(null, graph.nodes.get(2), "Selfloop Strategy test 6 failed");
        assertEquals(1, solutionSet.size(), "Selfloop Strategy test 7 failed");
        assertTrue(solutionSet.contains(v), "Selfloop Strategy test 8 failed");
    }

    @Test
    public void testEdgeBCCStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new EdgeBCCStrategy();
        assertEquals(14, graph.edgecount, "Edge BCC Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(1).neighbors.get(graph.nodes.get(6)), "Edge BCC Strategy test 2 failed");
        Command command = strategy.apply(graph).a;
        assertEquals(13, graph.edgecount, "Edge BCC Strategy test 3 failed");
        assertEquals(null, graph.nodes.get(1).neighbors.get(graph.nodes.get(6)), "Edge BCC Strategy test 4 failed");
        command.undo();
        assertEquals(14, graph.edgecount, "Edge BCC Strategy test 5 failed");
        assertNotEquals(null, graph.nodes.get(1).neighbors.get(graph.nodes.get(6)), "Edge BCC Strategy test 6 failed");
    }

    @Test
    public void testSingleAntlerStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new SingleAntlerStrategy();
        Node v = graph.nodes.get(4);
        assertEquals(8, graph.nodecount, "Single Antler Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(3), "Single Antler Strategy test 2 failed");
        assertNotEquals(null, graph.nodes.get(4), "Single Antler Strategy test 3 failed");
        Pair<Command, List<Node>> pair = strategy.apply(graph);
        Command command = pair.a;
        List<Node> solutionSet = pair.b;
        assertEquals(6, graph.nodecount, "Single Antler Strategy test 4 failed");
        assertEquals(null, graph.nodes.get(3), "Single Antler Strategy test 5 failed");
        assertEquals(null, graph.nodes.get(4), "Single Antler Strategy test 6 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Single Antler Strategy test 7 failed");
        assertNotEquals(null, graph.nodes.get(3), "Single Antler Strategy test 8 failed");
        assertNotEquals(null, graph.nodes.get(4), "Single Antler Strategy test 9 failed");
        assertEquals(1, solutionSet.size(), "Single Antler Strategy test 10 failed");
        assertTrue(solutionSet.contains(v), "Single Antler Strategy test 11 failed");
    }

    @Test
    public void testSingletonPathAntlerStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new SingletonPathAntlerStrategy();
        Node v = graph.nodes.get(4);
        graph.addNode(8);
        graph.addEdge(3, 8);
        graph.addEdge(4, 8, 2);
        assertEquals(9, graph.nodecount, "Singleton Path Antler Strategy test 1 failed");
        assertNotEquals(null, graph.nodes.get(4), "Singleton Path Antler Strategy test 2 failed");
        Pair<Command, List<Node>> pair = strategy.apply(graph);
        Command command = pair.a;
        List<Node> solutionSet = pair.b;
        assertEquals(8, graph.nodecount, "Single Antler Strategy test 3 failed");
        assertEquals(null, graph.nodes.get(4), "Single Antler Strategy test 4 failed");
        command.undo();
        assertEquals(9, graph.nodecount, "Single Antler Strategy test 5 failed");
        assertNotEquals(null, graph.nodes.get(4), "Single Antler Strategy test 6 failed");
        assertEquals(1, solutionSet.size(), "Single Antler Strategy test 7 failed");
        assertTrue(solutionSet.contains(v), "Single Antler Strategy test 8 failed");
    }

    @Test
    public void testCompositeKernalizationStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new CompositeKernalizationStrategy(new KernalizationStrategy[]{new IsolatedStrategy(), new LeafStrategy(), new Degree2Strategy(), new MultiEdgeStrategy(), new SelfloopStrategy()});
        Node v = graph.nodes.get(2);
        assertEquals(8, graph.nodecount, "Composite Kernalization Strategy test 1 failed");
        assertEquals(14, graph.edgecount, "Composite Kernalization Strategy test 2 failed");
        Pair<Command, List<Node>> pair = strategy.apply(graph);
        Command command = pair.a;
        List<Node> solutionSet = pair.b;
        assertEquals(4, graph.nodecount, "Composite Kernalization Strategy test 3 failed");
        assertEquals(5, graph.edgecount, "Composite Kernalization Strategy test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Composite Kernalization Strategy test 5 failed");
        assertEquals(14, graph.edgecount, "Composite Kernalization Strategy test 6 failed");
        assertEquals(1, solutionSet.size(), "Composite Kernalization Strategy test 7 failed");
        assertTrue(solutionSet.contains(v), "Composite Kernalization Strategy test 8 failed");
    }

    @Test
    public void testExhaustiveCompositeKernalizationStrategy() {
        Graph graph = makeTestGraph();
        KernalizationStrategy strategy = new CompositeKernalizationStrategy(new KernalizationStrategy[]{new IsolatedStrategy(), new LeafStrategy(), new Degree2Strategy(), new MultiEdgeStrategy(), new SelfloopStrategy()});
        Node v1 = graph.nodes.get(2);
        Node v2 = graph.nodes.get(4);
        assertEquals(8, graph.nodecount, "Exhaustive Composite Kernalization Strategy test 1 failed");
        assertEquals(14, graph.edgecount, "Exhaustive Composite Kernalization Strategy test 2 failed");
        Pair<Command, List<Node>> pair = strategy.exhaustiveApply(graph);
        Command command = pair.a;
        List<Node> solutionSet = pair.b;
        assertEquals(0, graph.nodecount, "Exhaustive Composite Kernalization Strategy test 3 failed");
        assertEquals(0, graph.edgecount, "Exhaustive Composite Kernalization Strategy test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Exhaustive Composite Kernalization Strategy test 5 failed");
        assertEquals(14, graph.edgecount, "Exhaustive Composite Kernalization Strategy test 6 failed");
        assertEquals(2, solutionSet.size(), "Exhaustive Composite Kernalization Strategy test 7 failed");
        assertTrue(solutionSet.contains(v1) && solutionSet.contains(v2), "Exhaustive Composite Kernalization Strategy test 8 failed");
    }
}
