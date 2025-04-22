package thesis.antlersolver.model;

import org.junit.jupiter.api.Test;

import fvs_wata_orz.Graph;

import static org.junit.jupiter.api.Assertions.*;

public class PathAntlerTest {

    public Graph makeTestGraph() {
        int[][] adj = new int[][]{{1,2,6},{0,2,5,6},{0,1,3,5,5},{2,4,4},{3,3,5,5},{1,2,2,4,4,6},{0,1,5}};
        return new Graph(adj);
    }

    public PathAntler makeTestPathAntler(Graph graph) {
        PathAntler pathAntler = new PathAntler(graph);
        pathAntler.addC(5);
        pathAntler.addC(6);
        pathAntler.addP(1);
        return pathAntler;
    }

    @Test
    public void testComputeStatistics() {
        Graph graph = makeTestGraph();
        PathAntler pathAntler = makeTestPathAntler(graph);
        pathAntler.computeStatistics();
        assertFalse(pathAntler.isCyclic, "Compute Statistics test 1 failed");
        assertEquals(1, pathAntler.endpoints[0], "Compute Statistics test 2 failed");
        assertEquals(1, pathAntler.endpoints[1], "Compute Statistics test 3 failed");
        assertEquals(0, pathAntler.nextnodes[0], "Compute Statistics test 4 failed");
        assertEquals(2, pathAntler.nextnodes[1], "Compute Statistics test 5 failed");
    }

    @Test
    public void testExtendP() {
        Graph graph = makeTestGraph();
        PathAntler pathAntler = makeTestPathAntler(graph);
        pathAntler.computeStatistics();
        pathAntler.extendP(false);
        assertFalse(pathAntler.isCyclic, "Extend P test 1 failed");
        assertEquals(0, pathAntler.endpoints[0], "Extend P test 2 failed");
        assertEquals(1, pathAntler.endpoints[1], "Extend P test 3 failed");
        assertEquals(2, pathAntler.nextnodes[0], "Extend P test 4 failed");
        assertEquals(2, pathAntler.nextnodes[1], "Extend P test 5 failed");
        assertEquals(2, pathAntler.getP().length, "Extend P test 6 failed");
        pathAntler.addC(3);
        pathAntler.extendP(false);
        assertTrue(pathAntler.isCyclic, "Extend P test 7 failed");
        assertEquals(-1, pathAntler.endpoints[0], "Extend P test 8 failed");
        assertEquals(-1, pathAntler.endpoints[1], "Extend P test 9 failed");
        assertEquals(-1, pathAntler.nextnodes[0], "Extend P test 10 failed");
        assertEquals(-1, pathAntler.nextnodes[1], "Extend P test 11 failed");
        assertEquals(3, pathAntler.getP().length, "Extend P test 12 failed");
        PathAntler pathAntler2 = new PathAntler(graph);
        pathAntler2.addC(0);
        pathAntler2.addC(4);
        pathAntler2.addC(5);
        pathAntler2.addP(1);
        pathAntler2.computeStatistics();
        pathAntler2.extendP(false);
        assertFalse(pathAntler2.isCyclic, "Extend P test 13 failed");
        assertEquals(3, pathAntler2.endpoints[0], "Extend P test 14 failed");
        assertEquals(6, pathAntler2.endpoints[1], "Extend P test 15 failed");
        assertEquals(-1, pathAntler2.nextnodes[0], "Extend P test 16 failed");
        assertEquals(-1, pathAntler2.nextnodes[1], "Extend P test 17 failed");
        assertEquals(4, pathAntler2.getP().length, "Extend P test 18 failed");
    }

    @Test
    public void testComputeA() {
        Graph graph = makeTestGraph();
        graph.addEdge(0, 4, 2);
        graph.addEdge(2, 4, 2);
        graph.addEdge(4, 6, 2);
        PathAntler pathAntler = new PathAntler(graph);
        pathAntler.addC(graph.nodes.get(1));
        pathAntler.addC(graph.nodes.get(4));
        pathAntler.addC(graph.nodes.get(5));
        pathAntler.addP(graph.nodes.get(0));
        pathAntler.computeStatistics();
        pathAntler.extendP(false);
        pathAntler.computeMaxA();
        assertEquals(1, pathAntler.getA().size(), "Compute A test 1 failed");
        assertTrue(pathAntler.getA().contains(graph.nodes.get(4)), "Compute A test 2 failed");
    }

    @Test
    public void testContainedEqual() {
        Graph graph = makeTestGraph();
        PathAntler pathAntler1 = new PathAntler(graph);
        PathAntler pathAntler2 = new PathAntler(graph);
        pathAntler1.addC(graph.nodes.get(1));
        pathAntler1.addC(graph.nodes.get(2));
        pathAntler1.addP(graph.nodes.get(0));
        pathAntler2.addC(graph.nodes.get(1));
        pathAntler2.addP(graph.nodes.get(0));
        pathAntler2.addP(graph.nodes.get(6));
        assertTrue(pathAntler2.contained(pathAntler1), "Contained Equal test 1 failed");
        assertFalse(pathAntler2.equals(pathAntler1), "Contained Equal test 2 failed");
        pathAntler1.removeC(graph.nodes.get(2));
        assertTrue(pathAntler2.contained(pathAntler1), "Contained Equal test 3 failed");
        assertFalse(pathAntler2.equals(pathAntler1), "Contained Equal test 4 failed");
        pathAntler2.removeP(graph.nodes.get(6));
        assertTrue(pathAntler2.contained(pathAntler1), "Contained Equal test 5 failed");
        assertTrue(pathAntler1.contained(pathAntler2), "Contained Equal test 6 failed");
        assertTrue(pathAntler2.equals(pathAntler1), "Contained Equal test 7 failed");
        pathAntler1.addC(graph.nodes.get(2));
        pathAntler2.addC(graph.nodes.get(5));
        assertFalse(pathAntler2.contained(pathAntler1), "Contained Equal test 8 failed");
        assertFalse(pathAntler1.contained(pathAntler2), "Contained Equal test 9 failed");
        assertFalse(pathAntler2.equals(pathAntler1), "Contained Equal test 10 failed");
    }
}
