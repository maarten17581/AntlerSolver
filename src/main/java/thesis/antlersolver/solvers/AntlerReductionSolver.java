package thesis.antlersolver.solvers;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fvs_wata_orz.Graph;
import fvs_wata_orz.HalfIntegralRelax;
import fvs_wata_orz.LowerBound;
import fvs_wata_orz.Reduction;
import fvs_wata_orz.ReductionRoot;
import fvs_wata_orz.Solver;
import fvs_wata_orz.tc.wata.data.*;
import fvs_wata_orz.tc.wata.debug.*;
import fvs_wata_orz.tc.wata.util.*;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.reductions.AntlerReduction;
import thesis.antlersolver.statistics.Statistics;

public class AntlerReductionSolver extends Solver {
	
	public static long counter = 0;

    void update2(int[] res) {
		ub = res.length;
		this.res = res;
		if (outputUB) System.err.printf("ub = %d%n", add + ub);
	}

    @Override
    public void solve(Graph g) {
        // ReductionRoot.reduce(g);
        // int last = 0;
        // do {
        //     if(Thread.currentThread().isInterrupted()) return;
        //     last = g.n();
        //     AntlerReduction.reduce(g);
        //     if(g.n() != last) ReductionRoot.reduce(g);
        // } while(g.n() != last);
        update2(GraphAlgorithm.heuristicFVS(g));
        solve_recurse(g);
    }
	
	public void solve_recurse(Graph g) {
        if(Thread.currentThread().isInterrupted()) return;
		ReductionRoot.reduce(g);
		if (ub <= LowerBound.lowerBound(g)) return;
		if (g.m() == 0) {
			update2(g.getS());
			return;
		}
		Graph[] gs = g.decompose(g.n() < g.n * 0.5);
		if (gs != null) {
			if (ReductionRoot.DEBUG) {
				int[] size = new int[gs.length];
				for (int i = 0; i < gs.length; i++) size[i] = gs[i].n;
				System.err.printf("decompose: %s%n", Utils.toString(size, " + "));
			}
			IntArray tmp = new IntArray();
			for (int i = 0; i < g.n; i++) if (g.used[i] == 'S') tmp.add(i);
			for (int i = 0; i < gs.length; i++) {
				Graph h = gs[i];
				AntlerReductionSolver solver = new AntlerReductionSolver();
				solver.ub = ub - tmp.length;
				if (outputUB && i == gs.length - 1) {
					solver.outputUB = true;
					solver.add = add + tmp.length;
				}
				solver.solve_recurse(h);
				if (solver.res == null) return;
				for (int j : solver.res) tmp.add(h.id[j]);
			}
			ub = tmp.length;
			res = tmp.toArray();
			return;
		}
		ReductionRoot.DEBUG = false;
		int s = -1;
		for (int i = 0; i < g.n; i++) if (g.adj[i].length > 0) {
			if (s < 0 || g.adj[s].length < g.adj[i].length) s = i;
		}
		count();
        Statistics.getStat().count("branch");
		Graph g1 = new Graph(g), g2 = g;
//		Debug.print("S", s);
		g1.setS(s);
		solve_recurse(g1);
        //if(res != null) return;
//		Debug.print("F", s);
		g2.setF(s);
		solve_recurse(g2, s);
	}
	
	public void solve_recurse(Graph g, int s) {
        if(Thread.currentThread().isInterrupted()) return;
		Reduction.reduce(g, ub);
		if (ub <= LowerBound.lowerBound(g)) return;
        int maxdegree = 0;
        for(int i = 0; i < g.n; i++) if(g.used[i] == 0) maxdegree = Math.max(maxdegree, g.adj[i].length);
        if (ub*maxdegree <= g.adj[s].length-2) return;
		if (g.m() == 0) {
			update2(g.getS());
			return;
		}
		if (g.adj[s].length == 0) {
			solve_recurse(g);
			return;
		}
		Graph[] gs = g.decompose(false);
		if (gs != null) {
			IntArray tmp = new IntArray();
			for (int i = 0; i < g.n; i++) if (g.used[i] == 'S') tmp.add(i);
			for (int i = 0; i < gs.length; i++) {
				Graph h = gs[i];
				AntlerReductionSolver solver = new AntlerReductionSolver();
				solver.ub = ub - tmp.length;
				if (outputUB && i == gs.length - 1) {
					solver.outputUB = true;
					solver.add = add + tmp.length;
				}
				int s2 = -1;
				for (int j = 0; j < h.n; j++) if (h.adj[j].length > 0 && h.used[j] == 'F') {
					s2 = j;
				}
                if (s2 >= 0) solver.solve_recurse(h, s2);
				else solver.solve_recurse(h);
				if (solver.res == null) return;
				for (int j : solver.res) tmp.add(h.id[j]);
			}
			ub = tmp.length;
			res = tmp.toArray();
			return;
		}
		if (ReductionRoot.LEVEL >= 2) {
			double[] x = new HalfIntegralRelax().solve(g, s);
			if (ub <= x[g.n] + 0.5) return;
			if (x[g.n] * 2 != g.adj[s].length) {
				int[] que = new int[g.n];
				int qs = 0, qt = 0;
				boolean[] used = new boolean[g.n];
				que[qt++] = s;
				used[s] = true;
				while (qs < qt) {
					int v = que[qs++];
					for (int u : g.adj[v]) if (x[u] == 0 && !used[u]) {
						que[qt++] = u;
						used[u] = true;
					}
				}
				for (int i = 1; i < qt; i++) g.contract(que[i], s);
				for (int i = 0; i < g.n; i++) if (x[i] == 1 && g.used[i] == 0) g.setS(i);
				solve_recurse(g, s);
				return;
			}
		}
		int v = -1;
		for (int u : g.adj[s]) {
			if (v < 0 || g.adj[v].length < g.adj[u].length) v = u;
		}
		count();
        Statistics.getStat().count("branch");
		Graph g1 = new Graph(g), g2 = g;
//		Debug.print("+S", v);
		g1.setS(v);
		solve_recurse(g1, s);
        //if(res != null) return;
//		Debug.print("+F", v);
		g2.contract(v, s);
		solve_recurse(g2, s);
	}
	
	void count() {
		if (Long.bitCount(++counter) == 1) Debug.print("#branch", counter);
	}
	
}
