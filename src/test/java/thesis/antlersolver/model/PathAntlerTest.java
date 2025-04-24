package thesis.antlersolver.model;

import org.junit.jupiter.api.Test;

import fvs_wata_orz.Graph;

import static org.junit.jupiter.api.Assertions.*;

public class PathAntlerTest {

    public Graph makeTestGraph() {
        int[][] adj = new int[][]{{1,2,6},{0,2,5,6},{0,1,3,5,5},{2,4,4},{3,3,5,5},{1,2,2,4,4,6},{0,1,5}};
        return new Graph(adj);
    }

    public Graph makeTestGraph2() {
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
        Graph graph = makeTestGraph2();
        PathAntler pathAntler = new PathAntler(graph);
        pathAntler.addC(1);
        pathAntler.addC(5);
        pathAntler.addP(0);
        pathAntler.computeStatistics();
        pathAntler.extendP(false);
        pathAntler.computeMaxA();
        assertEquals(2, pathAntler.getA().length, "Compute A test 1 failed");
        assertEquals(1, pathAntler.getA()[0], "Compute A test 2 failed");
        assertEquals(5, pathAntler.getA()[1], "Compute A test 3 failed");
    }

    @Test
    public void testContainedEqual() {
        Graph graph = makeTestGraph();
        PathAntler pathAntler1 = new PathAntler(graph);
        PathAntler pathAntler2 = new PathAntler(graph);
        pathAntler1.addC(1);
        pathAntler1.addC(2);
        pathAntler1.addP(0);
        pathAntler2.addC(1);
        pathAntler2.addP(0);
        pathAntler2.addP(6);
        assertFalse(pathAntler2.equals(pathAntler1), "Contained Equal test 1 failed");
        pathAntler1.removeC(2);
        assertFalse(pathAntler2.equals(pathAntler1), "Contained Equal test 2 failed");
        pathAntler2.removeP(6);
        assertTrue(pathAntler2.equals(pathAntler1), "Contained Equal test 3 failed");
        pathAntler1.addC(2);
        pathAntler2.addC(5);
        assertFalse(pathAntler2.equals(pathAntler1), "Contained Equal test 4 failed");
    }
}
