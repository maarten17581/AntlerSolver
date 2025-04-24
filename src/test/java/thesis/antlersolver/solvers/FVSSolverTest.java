package thesis.antlersolver.solvers;

import org.junit.jupiter.api.Test;

import fvs_wata_orz.FPTBranchingSolver;
import fvs_wata_orz.Graph;
import fvs_wata_orz.ReductionRoot;
import fvs_wata_orz.Solver;
import thesis.antlersolver.algorithm.GraphAlgorithm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class FVSSolverTest {

    public final int testingTime = 1000;

    public void testSolver(Solver solver, String solverName) throws Exception {
            Random rand = new Random();
            long time = 0;
            int size = 0;
            ReductionRoot.DEBUG = false;
            for(int i = 2; time < testingTime; i++) {
                Graph g1 = GraphAlgorithm.randomGraph(i, rand.nextDouble());
                Graph g2 = new Graph(g1);
                Graph g3 = new Graph(g1);
                long subtime = System.currentTimeMillis();
                TimedSolve timedSolver = new TimedSolve(solver.getClass().getDeclaredConstructor().newInstance());
                int[] fvs1 = timedSolver.solve(g1, testingTime-time);
                time += System.currentTimeMillis()-subtime;
                if(fvs1 == null) continue;
                if(i <= 15) {
                    int[] fvs2 = GraphAlgorithm.smartFVS(g2);
                    assertEquals(fvs2.length, fvs1.length, solverName+" test " + (2*i-3) + " failed");
                    assertTrue(GraphAlgorithm.isFVS(fvs1, g3), solverName+" test " + (2*i-2) + " failed");
                }
                size = i;
            }
            System.out.println(solverName+" test time: "+time+", until size: "+size);
    }

    @Test
    public void NaiveSolverTest() throws Exception {
        testSolver(new NaiveSolver(), "Naive Solver");
    }

    @Test
    public void AntlerReductionSolverTest() throws Exception {
        testSolver(new AntlerReductionSolver(), "Antler Reduction Solver");
    }

    @Test
    public void FPTBranchingSolverTest() throws Exception {
        testSolver(new FPTBranchingSolver(), "FPT Branching Solver");
    }
}
