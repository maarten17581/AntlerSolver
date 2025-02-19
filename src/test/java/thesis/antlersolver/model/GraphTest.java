package thesis.antlersolver.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

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
    public void testNodeCount() {
        Graph graph = makeTestGraph();
        assertEquals(8, graph.nodecount, "Node Count test failed");
    }

    @Test
    public void testEdgeCount() {
        Graph graph = makeTestGraph();
        assertEquals(14, graph.edgecount, "Edge Count test failed");
    }

    @Test
    public void testNodeDegree() {
        Graph graph = makeTestGraph();
        assertEquals(6, graph.nodes.get(2).degree, "Node Degree test 1 failed");
        graph.addEdge(1, 2);
        assertEquals(7, graph.nodes.get(2).degree, "Node Degree test 2 failed");
        graph.addEdge(2, 2);
        assertEquals(8, graph.nodes.get(2).degree, "Node Degree test 3 failed");
        graph.removeEdge(2, 2, 2);
        assertEquals(6, graph.nodes.get(2).degree, "Node Degree test 4 failed");
        graph.removeEdge(1, 2);
        assertEquals(5, graph.nodes.get(2).degree, "Node Degree test 5 failed");
    }

    @Test
    public void testNodeNbh() {
        Graph graph = makeTestGraph();
        assertEquals(4, graph.nodes.get(2).nbhSize, "Node Neighborhood test 1 failed");
        graph.addEdge(1, 2);
        assertEquals(4, graph.nodes.get(2).nbhSize, "Node Neighborhood test 2 failed");
        graph.addEdge(2, 2);
        assertEquals(4, graph.nodes.get(2).nbhSize, "Node Neighborhood test 3 failed");
        graph.removeEdge(2, 2, 2);
        assertEquals(4, graph.nodes.get(2).nbhSize, "Node Neighborhood test 4 failed");
        graph.removeAllEdges(1, 2);
        assertEquals(3, graph.nodes.get(2).nbhSize, "Node Neighborhood test 5 failed");
    }

    @Test
    public void testEdgeNumber() {
        Graph graph = makeTestGraph();
        assertEquals(3, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Edge Number 1 test failed");
        graph.addEdge(4, 5, 10);
        assertEquals(13, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Edge Number 2 test failed");
        graph.removeEdge(4, 5, 6);
        assertEquals(7, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Edge Number 3 test failed");
        graph.removeEdge(4,5, 7);
        assertEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Edge Number 4 test failed");
    }

    @Test
    public void testNodeRemove() {
        Graph graph = makeTestGraph();
        assertNotEquals(null, graph.nodes.get(1), "Node Remove test 1 failed");
        assertEquals(null, graph.removeNode(1), "Node Remove test 2 failed");
        graph.removeAllEdges(1, 0);
        graph.removeEdge(1, 2);
        graph.removeEdge(1, 5, 1);
        graph.removeEdge(1, 6);
        graph.removeNode(1);
        assertEquals(null, graph.nodes.get(1), "Node Remove test 3 failed");
        assertEquals(7, graph.nodecount, "Node Remove test 4 failed");
    }

    @Test
    public void testEdgeRemove() {
        Graph graph = makeTestGraph();
        assertNotEquals(null, graph.nodes.get(0).neighbors.get(graph.nodes.get(1)), "Edge Remove test 1 failed");
        graph.removeAllEdges(0, 1);
        assertEquals(null, graph.nodes.get(0).neighbors.get(graph.nodes.get(1)), "Edge Remove test 2 failed");
        assertNotEquals(null, graph.nodes.get(0).neighbors.get(graph.nodes.get(2)), "Edge Remove test 3 failed");
        graph.removeEdge(0, 2);
        assertEquals(null, graph.nodes.get(0).neighbors.get(graph.nodes.get(2)), "Edge Remove test 4 failed");
        assertNotEquals(null, graph.nodes.get(1).neighbors.get(graph.nodes.get(2)), "Edge Remove test 5 failed");
        graph.removeEdge(1, 2, 1);
        assertEquals(null, graph.nodes.get(1).neighbors.get(graph.nodes.get(2)), "Edge Remove test 6 failed");
        assertNotEquals(null, graph.nodes.get(3).neighbors.get(graph.nodes.get(4)), "Edge Remove test 7 failed");
        graph.removeAllEdges(3, 4);
        assertEquals(null, graph.nodes.get(3).neighbors.get(graph.nodes.get(4)), "Edge Remove test 8 failed");
        assertNotEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Edge Remove test 9 failed");
        graph.removeEdge(4, 5);
        assertNotEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Edge Remove test 10 failed");
        assertNotEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Edge Remove test 11 failed");
        graph.removeEdge(4, 5, 1);
        assertNotEquals(null, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)), "Edge Remove test 12 failed");
        assertEquals(7, graph.edgecount, "Edge Remove test 13 failed");
    }

    @Test
    public void testAddNode() {
        Graph graph = makeTestGraph();
        graph.addNode();
        assertEquals(9, graph.nodecount, "Node Add test 1 failed");
        assertEquals(null, graph.addNode(0), "Node Add test 2 failed");
        graph.addNode(9);
        assertEquals(10, graph.nodecount, "Node Add test 3 failed");
        assertNotEquals(null, graph.nodes.get(9), "Node Add test 4 failed");
    }

    @Test
    public void testAddEdge() {
        Graph graph = makeTestGraph();
        graph.addEdge(0, 0);
        assertEquals(15, graph.edgecount, "Edge Add test 1 failed");
        assertEquals(1, graph.nodes.get(0).neighbors.get(graph.nodes.get(0)).c, "Edge Add test 2 failed");
        assertEquals(2, graph.nodes.get(3).neighbors.get(graph.nodes.get(4)).c, "Edge Add test 3 failed");
        assertEquals(3, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Edge Add test 4 failed");
        graph.addEdge(4, 5, 10);
        assertEquals(13, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Edge Add test 5 failed");
        graph.addEdge(graph.nodes.get(4).neighbors.get(graph.nodes.get(5)));
        assertEquals(26, graph.nodes.get(4).neighbors.get(graph.nodes.get(5)).c, "Edge Add test 6 failed");
        assertEquals(38, graph.edgecount, "Edge Add test 7 failed");
    }

    @Test
    public void testGraphConstructor() {
        Graph graph = new Graph("test");
        assertEquals("test", graph.name, "Graph Constructor test 1 failed");
        assertEquals(0, graph.nodecount, "Graph Constructor test 2 failed");
        graph = new Graph("test", 10);
        assertEquals(10, graph.nodecount, "Graph Constructor test 3 failed");
        graph = new Graph("test", 10, 0);
        assertEquals(0, graph.edgecount, 1.1, "Graph Constructor test 4 failed");
        graph = new Graph("test", 10, 1);
        assertEquals(45, graph.edgecount, 1.1, "Graph Constructor test 5 failed");
        graph = new Graph("test", 100, 0.5);
        assertEquals(2475, graph.edgecount, 106, "Graph Constructor test 6 failed");
    }

    @Test
    public void testIsolatedSet() {
        Graph graph = makeTestGraph();
        assertEquals(1, graph.isolated.size(), "Isolated Set test 1 failed");
        assertTrue(graph.isolated.contains(graph.nodes.get(7)), "Isolated Set test 2 failed");
        graph.addNode(10);
        assertEquals(2, graph.isolated.size(), "Isolated Set test 3 failed");
        assertTrue(graph.isolated.contains(graph.nodes.get(7)), "Isolated Set test 4 failed");
        assertTrue(graph.isolated.contains(graph.nodes.get(10)), "Isolated Set test 5 failed");
        graph.removeNode(10);
        assertEquals(1, graph.isolated.size(), "Isolated Set test 6 failed");
        assertTrue(graph.isolated.contains(graph.nodes.get(7)), "Isolated Set test 7 failed");
    }

    @Test
    public void testLeaveSet() {
        Graph graph = makeTestGraph();
        assertEquals(1, graph.leaves.size(), "Leave Set test 1 failed");
        assertTrue(graph.leaves.contains(graph.nodes.get(6)), "Leave Set test 2 failed");
        graph.addEdge(6, 7);
        assertEquals(1, graph.leaves.size(), "Leave Set test 3 failed");
        assertTrue(graph.leaves.contains(graph.nodes.get(7)), "Leave Set test 5 failed");
        graph.removeEdge(6, 7);
        assertEquals(1, graph.leaves.size(), "Leave Set test 6 failed");
        assertTrue(graph.leaves.contains(graph.nodes.get(6)), "Leave Set test 7 failed");
    }

    @Test
    public void testDegree2Set() {
        Graph graph = makeTestGraph();
        assertEquals(1, graph.degree2.size(), "Degree 2 Set test 1 failed");
        assertTrue(graph.degree2.contains(graph.nodes.get(0)), "Degree 2 Set test 2 failed");
        graph.addEdge(0, 6);
        assertEquals(1, graph.degree2.size(), "Degree 2 Set test 3 failed");
        assertTrue(graph.degree2.contains(graph.nodes.get(6)), "Degree 2 Set test 4 failed");
        graph.removeEdge(0, 6);
        assertEquals(1, graph.degree2.size(), "Degree 2 Set test 5 failed");
        assertTrue(graph.degree2.contains(graph.nodes.get(0)), "Degree 2 Set test 6 failed");
    }

    @Test
    public void testSelfloopSet() {
        Graph graph = makeTestGraph();
        assertEquals(1, graph.selfloop.size(), "Selfloop Set test 1 failed");
        assertTrue(graph.selfloop.contains(graph.nodes.get(2)), "Selfloop Set test 2 failed");
        graph.addEdge(3, 6, 1);
        assertEquals(1, graph.selfloop.size(), "Selfloop Set test 3 failed");
        assertTrue(graph.selfloop.contains(graph.nodes.get(2)), "Selfloop Set test 4 failed");
        graph.addEdge(3, 3, 10);
        assertEquals(2, graph.selfloop.size(), "Selfloop Set test 5 failed");
        assertTrue(graph.selfloop.contains(graph.nodes.get(2)), "Selfloop Set test 6 failed");
        assertTrue(graph.selfloop.contains(graph.nodes.get(3)), "Selfloop Set test 7 failed");
        graph.removeEdge(3, 3, 8);
        assertEquals(2, graph.selfloop.size(), "Selfloop Set test 8 failed");
        assertTrue(graph.selfloop.contains(graph.nodes.get(2)), "Selfloop Set test 9 failed");
        assertTrue(graph.selfloop.contains(graph.nodes.get(3)), "Selfloop Set test 10 failed");
        graph.removeAllEdges(3, 3);
        graph.removeEdge(2, 2);
        assertEquals(0, graph.selfloop.size(), "Selfloop Set test 11 failed");
    }

    @Test
    public void testMoreThan2Set() {
        Graph graph = makeTestGraph();
        assertEquals(1, graph.multiEdge.size(), "More Than 2 Set test 1 failed");
        assertTrue(graph.multiEdge.contains(graph.nodes.get(4).neighbors.get(graph.nodes.get(5))), "More Than 2 Set test 2 failed");
        graph.addEdge(3, 6, 1);
        assertEquals(1, graph.multiEdge.size(), "More Than 2 Set test 3 failed");
        assertTrue(graph.multiEdge.contains(graph.nodes.get(4).neighbors.get(graph.nodes.get(5))), "More Than 2 Set test 4 failed");
        graph.addEdge(3, 6, 10);
        assertEquals(2, graph.multiEdge.size(), "More Than 2 Set test 5 failed");
        assertTrue(graph.multiEdge.contains(graph.nodes.get(4).neighbors.get(graph.nodes.get(5))), "More Than 2 Set test 6 failed");
        assertTrue(graph.multiEdge.contains(graph.nodes.get(3).neighbors.get(graph.nodes.get(6))), "More Than 2 Set test 7 failed");
        graph.removeEdge(3, 6, 9);
        assertEquals(1, graph.multiEdge.size(), "More Than 2 Set test 8 failed");
        assertTrue(graph.multiEdge.contains(graph.nodes.get(4).neighbors.get(graph.nodes.get(5))), "More Than 2 Set test 9 failed");
        graph.removeAllEdges(4, 5);
        assertEquals(0, graph.multiEdge.size(), "More Than 2 Set test 10 failed");
    }
}
