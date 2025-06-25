package thesis.antlersolver.solvers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fvs_wata_orz.Graph;
import fvs_wata_orz.Solver;
import thesis.antlersolver.statistics.Statistics;

public class TimedSolve {

    Solver solver;

    public TimedSolve(Solver solver) {
        this.solver = solver;
    }

    public int[] solve(Graph g, long time) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> solver.solve(g));

        int[] result = null;
        long startTime = System.currentTimeMillis();
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(() -> {
            if (!future.isDone()) {
                System.out.print("Solver is still running at " + (System.currentTimeMillis()-startTime) + " ms"
                    + " with current solution size " + (solver.res == null ? "-" : solver.res.length)
                    + " and branch count "+ Statistics.getStat().info.get("branch") +"\r");
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        try {
            future.get(time, TimeUnit.MILLISECONDS);
            result = solver.res;
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            monitor.shutdownNow(); // stop monitoring
            executor.shutdownNow(); // stop solver
            String removePrev = "";
            for(int i = 0; i < 100; i++) removePrev += " ";
            System.out.print(removePrev+"\r");
        }
        return result;
    }
}
