package thesis.antlersolver.reductions;

import java.util.Arrays;

import fvs_wata_orz.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.PathAntler;
import thesis.antlersolver.statistics.Statistics;

public class AntlerReduction {

    public static int K = 4;
    public static int singleTreeK = 6;
    public static int[] computeSize = new int[]{100, 50, 10};
    public static int P1 = 10;
    public static int P2 = 20;
    public static int Psize = 1000;
    public static boolean onlyLengthCheck = false;
    public static boolean onlyFlower = false;

    public static void reduce(Graph g) {
        singleAntler(g);
        singlePathAntlers(g);
        if(g.n() < 100 && singleTreeAntler(g, singleTreeK)) return;
        for(int i = 2; i <= K; i++) {
            if(kPathAntlers(g, i, onlyLengthCheck, Integer.MAX_VALUE)) return;
            if(g.n() < computeSize[i-2] && kAntler(g, i, onlyFlower)) return;
            if(g.n() >= computeSize[i-2]) break;
        }
        if(kPathAntlers(g, P1, onlyLengthCheck, Integer.MAX_VALUE)) return;
        kPathAntlers(g, P2, onlyLengthCheck, Psize);
	}

    static boolean singleAntler(Graph g) {
        int oldN = g.n();
        int[] queue = new int[g.n+2*g.m()];
        for(int i = 0; i < g.n; i++) queue[i] = i;
        int index = 0;
        int length = g.n;
        int[] N2 = new int[g.n];
        while(index < length) {
            int v = queue[index++];
            if(g.used[v] != 0) continue;
            int p = g.N2(v, N2);
            boolean n2reduction = false;
            for(int i = 0; i < p; i++) if (g.used[N2[i]] == 'F') {
                n2reduction = true;
                for(int w : g.adj[v]) queue[length++] = w;
                g.setS(v);
                break;
            }
            if(g.adj[v].length <= 2 && !n2reduction) {
                for(int w : g.adj[v]) queue[length++] = w;
                g.eliminate(v);
                continue;
            }
            if(g.adj[v].length == 3 && !n2reduction) {
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

    static boolean singlePathAntlers(Graph g) {
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

    static boolean kAntler(Graph g, int k, boolean onlyFlower) {
        int oldN = g.n();
        FVC[] fvcs = GraphAlgorithm.getKAntlers(k, g, onlyFlower);
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

    static boolean kPathAntlers(Graph g, int k, boolean onlyLengthCheck, int maxNumber) {
        int oldN = g.n();
        PathAntler[] pathAntlers = GraphAlgorithm.getKPathAntlers(k, g, onlyLengthCheck, maxNumber);
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

    static boolean singleTreeAntler(Graph g, int k) {
        int oldN = g.n();
        FVC[] fvcs = GraphAlgorithm.getSingleTreeAntlers(k, g);
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
}
