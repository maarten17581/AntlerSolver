package thesis.antlersolver.template;

import org.junit.jupiter.api.Test;

import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;

public class FVSSolverTest {

    public final int testingTime = 1000;

    @Test
    public void NaiveFVSSolverTest() {
        Random rand = new Random();
        AbstractFVSSolver solver = new NaiveFVSSolver();
        long time = 0;
        int size = 0;
        for(int i = 2; time < testingTime; i++) {
            Graph graph = new Graph("test", i, rand.nextDouble());
            long subtime = System.currentTimeMillis();
            List<Node> fvs1 = solver.solve(graph, subtime, testingTime-time);
            time += System.currentTimeMillis()-subtime;
            if(fvs1 == null) continue;
            List<Node> fvs2 = GraphAlgorithm.smartFVS(graph);
            assertEquals(fvs2.size(), fvs1.size(), "Naive FVS Solver test " + (i-1) + " failed");
            size = i;
        }
        System.out.println("Naive FVS Solver test time: "+time+", until size: "+size);
    }

    @Test
    public void MaxDegreeFVSSolverTest() {
        Random rand = new Random();
        AbstractFVSSolver solver = new MaxDegreeFVSSolver();
        long time = 0;
        int size = 0;
        for(int i = 2; time < testingTime; i++) {
            Graph graph = new Graph("test", i, rand.nextDouble());
            long subtime = System.currentTimeMillis();
            List<Node> fvs1 = solver.solve(graph, subtime, testingTime-time);
            time += System.currentTimeMillis()-subtime;
            if(fvs1 == null) continue;
            List<Node> fvs2 = GraphAlgorithm.smartFVS(graph);
            assertEquals(fvs2.size(), fvs1.size(), "Max Degree FVS Solver test " + (i-1) + " failed");
            size = i;
        }
        System.out.println("Max Degree Solver test time: "+time+", until size: "+size);
    }
}
