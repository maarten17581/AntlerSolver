package thesis.antlersolver.command;

import org.junit.jupiter.api.Test;

import thesis.antlersolver.model.Graph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class CommandTest {

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
    public void testAddNodeCommand() {
        Graph graph = makeTestGraph();
        Command command = new AddNodeCommand(10, graph);
        assertEquals(8, graph.nodecount, "Add Node Command test 1 failed");
        assertEquals(null, graph.nodes.get(10), "Add Node Command test 2 failed");
        command.execute();
        assertEquals(9, graph.nodecount, "Add Node Command test 3 failed");
        assertNotEquals(null, graph.nodes.get(10), "Add Node Command test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Add Node Command test 5 failed");
        assertEquals(null, graph.nodes.get(10), "Add Node Command test 6 failed");
    }

    @Test
    public void testRemoveNodeCommand() {
        Graph graph = makeTestGraph();
        Command command = new RemoveNodeCommand(5, graph);
        assertEquals(8, graph.nodecount, "Remove Node Command test 1 failed");
        assertEquals(14, graph.edgecount, "Remove Node Command test 2 failed");
        assertNotEquals(null, graph.nodes.get(5), "Add Node Command test 3 failed");
        command.execute();
        assertEquals(7, graph.nodecount, "Remove Node Command test 4 failed");
        assertEquals(8, graph.edgecount, "Remove Node Command test 5 failed");
        assertEquals(null, graph.nodes.get(5), "Add Node Command test 6 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Remove Node Command test 7 failed");
        assertEquals(14, graph.edgecount, "Remove Node Command test 8 failed");
        assertNotEquals(null, graph.nodes.get(5), "Add Node Command test 9 failed");
    }

    @Test
    public void testAddEdgeCommand() {
        Graph graph = makeTestGraph();
        Command command = new AddEdgeCommand(5, 6, 8, graph);
        assertEquals(14, graph.edgecount, "Add Edge Command test 1 failed");
        assertEquals(null, graph.nodes.get(5).neighbors.get(graph.nodes.get(6)), "Add Edge Command test 2 failed");
        command.execute();
        assertEquals(22, graph.edgecount, "Add Edge Command test 3 failed");
        assertNotEquals(null, graph.nodes.get(5).neighbors.get(graph.nodes.get(6)), "Add Edge Command test 4 failed");
        command.undo();
        assertEquals(14, graph.edgecount, "Add Edge Command test 5 failed");
        assertEquals(null, graph.nodes.get(5).neighbors.get(graph.nodes.get(6)), "Add Edge Command test 6 failed");
    }

    @Test
    public void testRemoveEdgeCommand() {
        Graph graph = makeTestGraph();
        Command command = new RemoveEdgeCommand(4, 5, 3, graph);
        assertEquals(14, graph.edgecount, "Remove Edge Command test 1 failed");
        assertNotEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Remove Edge Command test 2 failed");
        command.execute();
        assertEquals(11, graph.edgecount, "Remove Edge Command test 3 failed");
        assertEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Remove Edge Command test 4 failed");
        command.undo();
        assertEquals(14, graph.edgecount, "Remove Edge Command test 5 failed");
        assertNotEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Remove Edge Command test 6 failed");
    }

    @Test
    public void testCompositeCommand() {
        Graph graph = makeTestGraph();
        Command commandRemoveEdge = new RemoveEdgeCommand(4, 5, 3, graph);
        Command commandRemoveNode = new RemoveNodeCommand(5, graph);
        Command commandAddEdge = new AddEdgeCommand(2, 7, graph);
        Command commandAddNode = new AddNodeCommand(10, graph);
        Command command = new CompositeCommand(Arrays.asList(commandRemoveEdge, commandRemoveNode, commandAddEdge, commandAddNode));
        assertEquals(8, graph.nodecount, "Composite Command test 1 failed");
        assertEquals(14, graph.edgecount, "Composite Command test 2 failed");
        command.execute();
        assertEquals(8, graph.nodecount, "Composite Command test 3 failed");
        assertEquals(9, graph.edgecount, "Composite Command test 4 failed");
        command.undo();
        assertEquals(8, graph.nodecount, "Composite Command test 5 failed");
        assertEquals(14, graph.edgecount, "Composite Command test 6 failed");
    }
}
