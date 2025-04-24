package thesis.antlersolver.solvers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fvs_wata_orz.Graph;
import fvs_wata_orz.Solver;

public class TimedSolve {

    Solver solver;

    public TimedSolve(Solver solver) {
        this.solver = solver;
    }

    public int[] solve(Graph g, long time) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> solver.solve(g));

        int[] result = null;

        try {
            future.get(time, TimeUnit.MILLISECONDS);
            result = solver.res;
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
        return result;
    }
}
