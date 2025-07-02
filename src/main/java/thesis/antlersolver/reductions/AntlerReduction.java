package thesis.antlersolver.reductions;

import java.util.Arrays;

import fvs_wata_orz.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.PathAntler;
import thesis.antlersolver.statistics.Statistics;

public class AntlerReduction {

    public static int K = 4;
    public static int KH = 20;
    public static int[] computeSize = new int[]{100, 50, 10};
    public static int P1 = 10;
    public static int P2 = 20;
    public static int Psize = 1000;
    public static int T = 10;
    public static boolean onlyLengthCheck = false;
    public static boolean onlyFlower = false;
    public static boolean fromHeuristicSolve = false;

    public static void reduce(Graph g) {
        loop : while(g.n() > 0) {
            System.out.print("Antler Kernel Graph stats: " + g.n() + " " + g.m() + " " + g.k
                + "                                                  \r");
            if(Thread.currentThread().isInterrupted()) return;
            singleAntler(g);
            if(singlePathAntlers(g)) continue loop;
            if(g.n() == 0) break;
            for(int i = 2; i < KH; i++) for(HeuristicType type : HeuristicType.values()) {
                if(kAntlerHeuristic(g, i, onlyFlower, fromHeuristicSolve, 10, type)) continue loop;
            }
            if(kPathAntlers(g, P1, onlyLengthCheck, Integer.MAX_VALUE)) continue loop;
            if(kPathAntlers(g, P2, onlyLengthCheck, Psize)) continue loop;
            for(int i = 2; i < T; i++) {
                if(g.n() < 100 && singleTreeAntler(g, i)) continue loop;
                if(g.n() >= 100) break;
            }
            for(int i = 2; i <= K; i++) {
                if(kPathAntlers(g, i, onlyLengthCheck, Integer.MAX_VALUE)) continue loop;
                if(g.n() < computeSize[i-2] && kAntler(g, i, onlyFlower)) continue loop;
                if(g.n() >= computeSize[i-2]) break;
            }
            break;
        }
	}

    public static boolean singleAntler(Graph g) {
        int oldN = g.n();
        int[] queue = new int[g.n+2*g.m()];
        for(int i = 0; i < g.n; i++) queue[i] = i;
        int index = 0;
        int length = g.n;
        int[] N2 = new int[g.n];
        while(index < length) {
            int v = queue[index++];
            if(g.used[v] != 0) continue;
            if(g.adj[v].length == 0) continue;
            if(g.hasEdge(v, v) > 0) {
                for(int w : g.adj[v]) queue[length++] = w;
                g.setS(v);
                continue;
            }
            int p = g.N2(v, N2);
            boolean n2reduction = false;
            for(int i = 0; i < p; i++) if (g.used[N2[i]] == 'F') {
                n2reduction = true;
                for(int w : g.adj[v]) queue[length++] = w;
                g.setS(v);
                break;
            }
            if(n2reduction) continue;
            if(g.adj[v].length <= 2) {
                for(int w : g.adj[v]) queue[length++] = w;
                g.eliminate(v);
                continue;
            }
            if(g.adj[v].length == 3) {
                int x = g.adj[v][0];
                int y = g.adj[v][1];
                int z = g.adj[v][2];
                if(x == y || y == z) {
                    for(int w : g.adj[v]) queue[length++] = w;
                    g.setS(y);
                    Statistics.getStat().count("1Antler");
                    Statistics.getStat().count("1AntlerSize");
                }
            }
        }
        int newN = g.n();
		return oldN != newN;
    }

    public static boolean singlePathAntlers(Graph g) {
        int oldN = g.n();
        PathAntler[] pathAntlers = GraphAlgorithm.getSingletonPathAntlers(g);
        Arrays.sort(pathAntlers, (path1, path2) -> path2.getA().length - path1.getA().length);
        boolean[] used = new boolean[g.n];
        for(PathAntler path : pathAntlers) {
            int aSize = 0;
            for(int v : path.getA()) {
                if(used[v]) continue;
                g.setS(v);
                aSize++;
                used[v] = true;
            }
            for(int v : path.getC()) used[v] = true;
            for(int v : path.getP()) used[v] = true;
            if(aSize >= 1) {
                Statistics.getStat().count(path.getC().length+"PathAntler");
                Statistics.getStat().count(path.getC().length+"PathAntlerSize", aSize);
            }
        }
        int newN = g.n();
        return oldN != newN;
    }

    public static boolean kAntler(Graph g, int k, boolean onlyFlower) {
        int oldN = g.n();
        FVC[] fvcs = GraphAlgorithm.getKAntlers(k, g, onlyFlower);
        if(Thread.currentThread().isInterrupted()) return false;
        Arrays.sort(fvcs, (fvc1, fvc2) -> fvc2.getA().length - fvc1.getA().length);
        boolean[] used = new boolean[g.n];
        for(FVC fvc : fvcs) {
            int aSize = 0;
            for(int v : fvc.getA()) {
                if(used[v]) continue;
                g.setS(v);
                aSize++;
                used[v] = true;
            }
            for(int v : fvc.getC()) used[v] = true;
            for(int v : fvc.getF()) used[v] = true;
            if(aSize >= 1) {
                Statistics.getStat().count(fvc.getC().length+"Antler");
                Statistics.getStat().count(fvc.getC().length+"AntlerSize", aSize);
            }
        }
        int newN = g.n();
		return oldN != newN;
    }

    public static boolean kPathAntlers(Graph g, int k, boolean onlyLengthCheck, int maxNumber) {
        int oldN = g.n();
        PathAntler[] pathAntlers = GraphAlgorithm.getKPathAntlers(k, g, onlyLengthCheck, maxNumber);
        if(Thread.currentThread().isInterrupted()) return false;
        Arrays.sort(pathAntlers, (path1, path2) -> path2.getA().length - path1.getA().length);
        boolean[] used = new boolean[g.n];
        for(PathAntler path : pathAntlers) {
            int aSize = 0;
            for(int v : path.getA()) {
                if(used[v]) continue;
                g.setS(v);
                aSize++;
                used[v] = true;
            }
            for(int v : path.getC()) used[v] = true;
            for(int v : path.getP()) used[v] = true;
            if(aSize >= 1) {
                Statistics.getStat().count(path.getC().length+"PathAntler");
                Statistics.getStat().count(path.getC().length+"PathAntlerSize", aSize);
            }
        }
        int newN = g.n();
        return oldN != newN;
    }

    public static boolean singleTreeAntler(Graph g, int k) {
        int oldN = g.n();
        FVC[] fvcs = GraphAlgorithm.getSingleTreeAntlers(k, g);
        if(Thread.currentThread().isInterrupted()) return false;
        Arrays.sort(fvcs, (fvc1, fvc2) -> fvc2.getA().length - fvc1.getA().length);
        boolean[] used = new boolean[g.n];
        for(FVC fvc : fvcs) {
            int aSize = 0;
            for(int v : fvc.getA()) {
                if(used[v]) continue;
                if(g.used[v] > 0) System.out.println(g.used[v]);
                g.setS(v);
                aSize++;
                used[v] = true;
            }
            for(int v : fvc.getC()) used[v] = true;
            for(int v : fvc.getF()) used[v] = true;
            if(aSize >= 1) {
                Statistics.getStat().count(fvc.getC().length+"SingleTreeAntler");
                Statistics.getStat().count(fvc.getC().length+"SingleTreeAntlerSize", aSize);
            }
        }
        int newN = g.n();
		return oldN != newN;
    }

    public enum HeuristicType {
        F,
        Flower,
        Edge,
        Diameter,
    }

    public static boolean kAntlerHeuristic(Graph g, int k, boolean onlyFlower, boolean fromHeuristicSolve, int tries, HeuristicType type) {
        int oldN = g.n();
        if(Thread.currentThread().isInterrupted()) return false;
        for(int i = 0; i < tries; i++) {
            FVC fvc = null;
            switch (type) {
                case F:
                    fvc = GraphAlgorithm.getKAntlerHeuristicF(k, g, onlyFlower, fromHeuristicSolve);
                    break;
                case Flower:
                    fvc = GraphAlgorithm.getKAntlerHeuristicFlower(k, g, onlyFlower, fromHeuristicSolve);
                    break;
                case Edge:
                    fvc = GraphAlgorithm.getKAntlerHeuristicEdge(k, g, onlyFlower, fromHeuristicSolve);
                    break;
                case Diameter:
                    fvc = GraphAlgorithm.getKAntlerHeuristicDiameter(k, g, onlyFlower, fromHeuristicSolve);
                    break;
                default:
                    break;
            }
            if(fvc.getA().length == 0) continue;
            for(int v : fvc.getA()) {
                g.setS(v);
            }
            if(fromHeuristicSolve) {
                Statistics.getStat().count(fvc.getC().length+"AntlerHeuristic"+type+"FromSol");
                Statistics.getStat().count(fvc.getC().length+"AntlerHeuristic"+type+"FromSolSize", fvc.getA().length);
            } else {
                Statistics.getStat().count(fvc.getC().length+"AntlerHeuristic"+type);
                Statistics.getStat().count(fvc.getC().length+"AntlerHeuristic"+type+"Size", fvc.getA().length);
            }
            break;
        }
        int newN = g.n();
		return oldN != newN;
    }
}
