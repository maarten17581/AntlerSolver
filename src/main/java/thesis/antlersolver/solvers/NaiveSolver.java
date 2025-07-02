package thesis.antlersolver.solvers;
import java.util.Arrays;

import fvs_wata_orz.Graph;
import fvs_wata_orz.Solver;
import fvs_wata_orz.tc.wata.debug.*;

public class NaiveSolver extends Solver {
	
	public static long counter = 0;

    void update2(int[] res) {
		ub = res.length;
		this.res = res;
		if (outputUB) System.err.printf("ub = %d%n", add + ub);
	}
	
	@Override
	public void solve(Graph g) {
        if(g.k >= ub) return;
		if (g.m() == 0) {
			update2(g.getS());
			return;
		}
        for(int i = 0; i < g.n; i++) if(g.hasEdge(i, i) >= 1) g.setS(i);
		int s = -1;
		for (int i = 0; i < g.n; i++) if (g.adj[i].length > 0) {
			s = i;
            break;
		}
		count();
		Graph g1 = new Graph(g), g2 = g;
//		Debug.print("S", s);
		g1.setS(s);
		solve(g1);
//		Debug.print("F", s);
		g2.setF(s);
		solve(g2, s);
	}
	
	public void solve(Graph g, int s) {
        if(g.k >= ub) return;
		if (g.m() == 0) {
			update2(g.getS());
			return;
		}
		if (g.adj[s].length == 0) {
			solve(g);
			return;
		}
        for(int i = 0; i < g.n; i++) if(g.hasEdge(i, i) >= 1) g.setS(i);
        for(int v : g.adj[s]) if(g.hasEdge(v, s) >= 2) g.setS(v);
		int v = g.adj[s][0];
		count();
		Graph g1 = new Graph(g), g2 = g;
//		Debug.print("+S", v);
		g1.setS(v);
		solve(g1, s);
//		Debug.print("+F", v);
		g2.contract(v, s);
		solve(g2, s);
	}
	
	void count() {
		if (Long.bitCount(++counter) == 1) Debug.print("#branch", counter);
	}
	
}
