package thesis.antlersolver.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fvs_wata_orz.Graph;
import fvs_wata_orz.tc.wata.data.IntArray;
import thesis.antlersolver.model.Description;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;

public class GraphAlgorithm {

    public static Graph connectGraphs(Graph[] graphs, int[] e1, int[] e2) {
        int n = 0;
        for(int i = 0; i < graphs.length; i++) n += graphs[i].n;
        IntArray[] adjmake = new IntArray[n];
        for(int i = 0; i < n; i++) adjmake[i] = new IntArray();
        int index = 0;
        int extra = 0;
        for(int i = 0; i < graphs.length; i++) {
            for(int j = 0; j < graphs[i].n; j++) {
                for(int v : graphs[i].adj[j]) adjmake[index].add(v+extra);
                index++;
            }
            extra += graphs[i].n;
        }
        for(int i = 0; i < e1.length; i++) {
            adjmake[e1[i]].add(e2[i]);
            adjmake[e2[i]].add(e1[i]);
        }
        int[][] adj = new int[n][];
        for(int i = 0; i < n; i++) {
            adj[i] = adjmake[i].toArray();
            Arrays.sort(adj[i]);
        }
        return new Graph(adj);
    }

    public static Graph randomGraph(int n, double p) {
        Random rand = new Random();
        IntArray[] adjmake = new IntArray[n];
        for(int i = 0; i < n; i++) adjmake[i] = new IntArray();
        for(int i = 0; i < n; i++) {
            for(int j = i+1; j < n; j++) {
                if(rand.nextDouble() < p) {
                    adjmake[i].add(j);
                    adjmake[j].add(i);
                }
            }
        }
        int[][] adj = new int[n][];
        for(int i = 0; i < n; i++) {
            adj[i] = adjmake[i].toArray();
            Arrays.sort(adj[i]);
        }
        return new Graph(adj);
    }

    public static Graph randomTree(int n) {
        int[] nodecount = new int[n];
        int[] lengths = new int[n];
        int[] neighbor = new int[n-1];
        Random rand = new Random();
        for(int i = 0; i < n-1; i++) {
            neighbor[i] = rand.nextInt(i+1);
            nodecount[neighbor[i]]++;
        }
        int[][] adj = new int[n][];
        for(int i = 0; i < n; i++) {
            if(i == 0) adj[i] = new int[nodecount[i]];
            else {
                adj[i] = new int[nodecount[i]+1];
                adj[i][lengths[i]++] = neighbor[i-1];
                adj[neighbor[i-1]][lengths[neighbor[i-1]]++] = i;
            }
        }
        return new Graph(adj);
    }

    public static Graph randomForest(int n, int c) {
        int removed = 0;
        Graph t = randomTree(n);
        Random rand = new Random();
        while(removed < c-1) {
            int node = rand.nextInt(n);
            if(t.adj[node].length == 0) continue;
            int j1 = rand.nextInt(t.adj[node].length);
            int nb = t.adj[node][j1];
            int j2 = Arrays.binarySearch(t.adj[nb], node);
            int[] a = new int[t.adj[node].length-1];
            System.arraycopy(t.adj[node], 0, a, 0, j1);
            System.arraycopy(t.adj[node], j1+1, a, j1, t.adj[node].length-j1-1);
            t.adj[node] = a;
            a = new int[t.adj[nb].length-1];
            System.arraycopy(t.adj[nb], 0, a, 0, j2);
            System.arraycopy(t.adj[nb], j2+1, a, j2, t.adj[nb].length-j2-1);
            t.adj[nb] = a;
            removed++;
        }
        return t;
    }

    public static Graph randomForest(int n, double p) {
        int c = 1;
        Random rand = new Random();
        while(c < n-1 && rand.nextDouble() < p) c++;
        return randomForest(n, c);
    }

    /**
     * Creats a random graph with antler structures within it
     * 
     * @param f The size of the forests of the antler structures
     * @param k The size of the heads of the antler structures
     * @param n The size of the rest of the graph, outside of the antler structures
     * @param m The number of antlers to add to the graph
     * @param c The probability of splitting the tree within a forest, on average one gets 1/(1-c) trees in the forest
     * @param fk The density of edges between f and k
     * @param kk The density of edges between nodes in k
     * @param r The density of edges between k and n, and between nodes in n
     * @param t The probability of a tree in f connecting to a node in n
     * @return A graph that creates m antlers and connects all the antlers with the rest graph
     */
    public static Graph randomAntlerGraph(int f, int k, int n, int m, double c, double fk, double kk, double r, double t) {
        Graph[] forests = new Graph[m];
        Graph[] heads = new Graph[m];
        for(int i = 0; i < m; i++) {
            forests[i] = randomForest(f, c);
            heads[i] = randomGraph(k, kk);
        }
        Graph rest = randomGraph(n, r);
        Graph[] antlers = new Graph[m];
        Random rand = new Random();
        for(int l = 0; l < m; l++) {
            int edgeLength = 0;
            int[] e1 = new int[f*k];
            int[] e2 = new int[f*k];
            for(int i = 0; i < f; i++) for(int j = f; j < f+k; j++) if(rand.nextDouble() < fk) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            e1 = Arrays.copyOf(e1, edgeLength);
            e2 = Arrays.copyOf(e2, edgeLength);
            antlers[l] = connectGraphs(new Graph[]{forests[l], heads[l]}, e1, e2);
            System.out.print(("Done with m: "+l)+"\r");
        }
        return randomAntlerGraph(antlers, f, k, rest, r, t);
    }

    /**
     * Creats a random graph with antler structures within it
     * 
     * @param f The size of the forests of the antler structures
     * @param k The size of the heads of the antler structures
     * @param n The size of the rest of the graph, outside of the antler structures
     * @param m The number of antlers to add to the graph
     * @param c The probability of splitting the tree within a forest, on average one gets 1/(1-c) trees in the forest
     * @param a The minimum size of A in the maximal sub-antler (A,C,F)
     * @param r The density of edges between k and n, and between nodes in n
     * @param t The probability of a tree in f connecting to a node in n
     * @return A graph that creates m antlers and connects all the antlers with the rest graph
     */
    public static Graph randomAntlerGraph(int f, int k, int n, int m, double c, int a, double r, double t) {
        Graph[] forests = new Graph[m];
        Graph[] heads = new Graph[m];
        for(int i = 0; i < m; i++) {
            forests[i] = randomForest(f, c);
            heads[i] = new Graph(new int[k][0]);
        }
        Graph rest = randomGraph(n, r);
        Graph[] antlers = new Graph[m];
        int[] headPart = new int[k];
        for(int i = 0; i < k; i++) headPart[i] = i+f;
        for(int l = 0; l < m; l++) {
            int edgeLength = 0;
            List<Integer> order = new ArrayList<>();
            for(int i = 0; i < f*k+k*(k-1)/2; i++) order.add(i);
            Collections.shuffle(order);
            int[] e1 = new int[f*k+k*(k-1)/2];
            int[] e2 = new int[f*k+k*(k-1)/2];
            for(int i = 0; i < f; i++) for(int j = f; j < f+k; j++) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            for(int i = f; i < f+k; i++) for(int j = i+1; j < f+k; j++) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            int[] e1new = new int[e1.length];
            int[] e2new = new int[e2.length];
            for(int i = 0; i < e1.length; i++) e1new[order.get(i)] = e1[i];
            for(int i = 0; i < e2.length; i++) e2new[order.get(i)] = e2[i];
            e1 = e1new;
            e2 = e2new;
            int x = 0;
            int y = order.size();
            Graph antler = null;
            while(y-x > 1) {
                int mid = (x+y)/2;
                antler = connectGraphs(new Graph[]{forests[l], heads[l]}, Arrays.copyOf(e1, mid), Arrays.copyOf(e2, mid));
                FVC fvc = new FVC(antler, headPart);
                fvc.computeMaxA();
                if(fvc.getA().length >= a) {
                    y = mid;
                } else {
                    x = mid;
                }
            }
            antlers[l] = connectGraphs(new Graph[]{forests[l], heads[l]}, Arrays.copyOf(e1, y), Arrays.copyOf(e2, y));
            System.out.print(("Done with m: "+l)+"\r");
        }
        return randomAntlerGraph(antlers, f, k, rest, r, t);
    }

    /**
     * Creats a random graph with antler structures within it
     * 
     * @param antlers A list of antler graphs
     * @param f The size of the forests in the antler graphs
     * @param k The size of the head in the antler grpahs
     * @param rest A graph for the rest part of the graph
     * @param r The density of edges between k and n, and between nodes in n
     * @param t The probability of a tree in f connecting to a node in n
     * @return A graph that connects all the antlers with the rest graph
     */
    public static Graph randomAntlerGraph(Graph[] antlers, int f, int k, Graph rest, double r, double t) {
        System.out.print("Start combining\r");
        int n = rest.n;
        int m = antlers.length;
        int edgeLength = 0;
        int[] e1 = new int[k*n*m+k*k*m*m+f*m];
        int[] e2 = new int[k*n*m+k*k*m*m+f*m];
        Random rand = new Random();
        for(int l = 0; l < m; l++) {
            for(int i = f*(l+1)+k*l; i < (f+k)*(l+1); i++) for(int j = f*m+k*m; j < f*m+k*m+n; j++) if(rand.nextDouble() < r) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
        }
        for(int l1 = 0; l1 < m; l1++) {
            for(int l2 = l1+1; l2 < m; l2++) {
                for(int i = f*(l1+1)+k*l1; i < (f+k)*(l1+1); i++) for(int j = f*(l2+1)+k*l2; j < (f+k)*(l2+1); j++) if(rand.nextDouble() < r) {
                    e1[edgeLength] = i;
                    e2[edgeLength++] = j;
                }
            }
        }
        int[] forestPart = new int[f];
        for(int i = 0; i < f; i++) forestPart[i] = i;
        for(int l = 0; l < m; l++) {
            Graph forest = GraphAlgorithm.subGraph(forestPart, antlers[l]);
            int comps = f-forest.m();
            int cc = 0;
            int[] compCounter = new int[comps];
            int[] whichComp = new int[f];
            int[] q = new int[f];
            for(int i = 0; i < f; i++) {
                if(whichComp[i] > 0) continue;
                q[0] = i;
                compCounter[cc]++;
                whichComp[i] = cc+1;
                int index = 0;
                int length = 1;
                while(index < length) {
                    int next = q[index++];
                    for(int nb : forest.adj[next]) {
                        if(whichComp[nb] > 0) continue;
                        whichComp[nb] = cc+1;
                        compCounter[cc]++;
                        q[length++] = nb;
                    }
                }
                cc++;
            }
            for(int i = 0; i < comps; i++) {
                if(rand.nextDouble() > t) continue;
                int indexT = rand.nextInt(compCounter[i]);
                int count = 0;
                int treeNode = -1;
                for(int j = 0; j < f; j++) {
                    if(whichComp[j] == i+1) count++;
                    if(count > indexT) {
                        treeNode = (f+k)*l+j;
                        break;
                    }
                }
                int restNode = rand.nextInt(n)+f*m+k*m;
                e1[edgeLength] = treeNode;
                e2[edgeLength++] = restNode;
            }
        }
        Graph[] toConnect = new Graph[m+1];
        for(int i = 0; i < m; i++) toConnect[i] = antlers[i];
        toConnect[m] = rest;
        e1 = Arrays.copyOf(e1, edgeLength);
        e2 = Arrays.copyOf(e2, edgeLength);
        return connectGraphs(toConnect, e1, e2);
    }

    /**
     * Creats a random graph with path-antler structures within it
     * 
     * @param p The size of the paths of the path-antler structures
     * @param k The size of the heads of the path-antler structures
     * @param n The size of the rest of the graph, outside of the path-antler structures
     * @param m The number of path-antlers to add to the graph
     * @param fk The density of edges between f and k
     * @param kk The density of edges between nodes in k
     * @param r The density of edges between k and n, and between nodes in n
     * @param t The probability of a tree in f connecting to a node in n
     * @return A graph that creates m path-antlers and connects all the path-antlers with the rest graph
     */
    public static Graph randomPathAntlerGraph(int p, int k, int n, int m, double pk, double kk, double r, double t) {
        Graph[] paths = new Graph[m];
        Graph[] heads = new Graph[m];
        for(int i = 0; i < m; i++) {
            int[][] pathAdj = new int[p][];
            for(int j = 0; j < p; j++) {
                if(j == 0) pathAdj[j] = new int[]{j+1};
                else if(j == p-1) pathAdj[j] = new int[]{j-1};
                else pathAdj[j] = new int[]{j-1, j+1};
            }
            paths[i] = new Graph(pathAdj);
            heads[i] = randomGraph(k, kk);
        }
        Graph rest = randomGraph(n, r);
        Graph[] pathAntlers = new Graph[m];
        Random rand = new Random();
        for(int l = 0; l < m; l++) {
            int edgeLength = 0;
            int[] e1 = new int[p*k];
            int[] e2 = new int[p*k];
            for(int i = 0; i < p; i++) for(int j = p; j < p+k; j++) if(rand.nextDouble() < pk) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            e1 = Arrays.copyOf(e1, edgeLength);
            e2 = Arrays.copyOf(e2, edgeLength);
            pathAntlers[l] = connectGraphs(new Graph[]{paths[l], heads[l]}, e1, e2);
            System.out.print(("Done with m: "+l)+"\r");
        }
        return randomPathAntlerGraph(pathAntlers, p, k, rest, r, t);
    }

    /**
     * Creats a random graph with antler structures within it
     * 
     * @param p The size of the forests of the antler structures
     * @param k The size of the heads of the antler structures
     * @param n The size of the rest of the graph, outside of the antler structures
     * @param m The number of antlers to add to the graph
     * @param c The probability of splitting the tree within a forest, on average one gets 1/(1-c) trees in the forest
     * @param a The minimum size of A in the maximal sub-antler (A,C,F)
     * @param r The density of edges between k and n, and between nodes in n
     * @param t The probability of a tree in f connecting to a node in n
     * @return A graph that creates m antlers and connects all the antlers with the rest graph
     */
    public static Graph randomPathAntlerGraph(int p, int k, int n, int m, int a, double r, double t) {
        Graph[] paths = new Graph[m];
        Graph[] heads = new Graph[m];
        for(int i = 0; i < m; i++) {
            int[][] pathAdj = new int[p][];
            for(int j = 0; j < p; j++) {
                if(j == 0) pathAdj[j] = new int[]{j+1};
                else if(j == p-1) pathAdj[j] = new int[]{j-1};
                else pathAdj[j] = new int[]{j-1, j+1};
            }
            paths[i] = new Graph(pathAdj);
            heads[i] = new Graph(new int[k][0]);
        }
        Graph rest = randomGraph(n, r);
        Graph[] pathAntlers = new Graph[m];
        int[] pathPart = new int[p];
        for(int i = 0; i < p; i++) pathPart[i] = i;
        int[] headPart = new int[k];
        for(int i = 0; i < k; i++) headPart[i] = i+p;
        for(int l = 0; l < m; l++) {
            int edgeLength = 0;
            List<Integer> order = new ArrayList<>();
            for(int i = 0; i < p*k+k*(k-1)/2; i++) order.add(i);
            Collections.shuffle(order);
            int[] e1 = new int[p*k+k*(k-1)/2];
            int[] e2 = new int[p*k+k*(k-1)/2];
            for(int i = 0; i < p; i++) for(int j = p; j < p+k; j++) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            for(int i = p; i < p+k; i++) for(int j = i+1; j < p+k; j++) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            int[] e1new = new int[e1.length];
            int[] e2new = new int[e2.length];
            for(int i = 0; i < e1.length; i++) e1new[order.get(i)] = e1[i];
            for(int i = 0; i < e2.length; i++) e2new[order.get(i)] = e2[i];
            e1 = e1new;
            e2 = e2new;
            int x = 0;
            int y = order.size();
            Graph pathAntler = null;
            while(y-x > 1) {
                int mid = (x+y)/2;
                pathAntler = connectGraphs(new Graph[]{paths[l], heads[l]}, Arrays.copyOf(e1, mid), Arrays.copyOf(e2, mid));
                PathAntler pa = new PathAntler(pathAntler, new int[0], headPart, pathPart);
                pa.computeStatistics();
                pa.computeMaxA();
                if(pa.getA().length >= a) {
                    y = mid;
                } else {
                    x = mid;
                }
            }
            pathAntlers[l] = connectGraphs(new Graph[]{paths[l], heads[l]}, Arrays.copyOf(e1, y), Arrays.copyOf(e2, y));
            System.out.print(("Done with m: "+l)+"\r");
        }
        return randomPathAntlerGraph(pathAntlers, p, k, rest, r, t);
    }

    /**
     * Creats a random graph with path-antler structures within it
     * 
     * @param pathAntlers A list of path-antler graphs
     * @param p The size of the paths in the path-antler graphs
     * @param k The size of the head in the path-antler grpahs
     * @param rest A graph for the rest part of the graph
     * @param r The density of edges between k and n, and between nodes in n
     * @param t The probability of a tree in f connecting to a node in n
     * @return A graph that connects all the path-antlers with the rest graph
     */
    public static Graph randomPathAntlerGraph(Graph[] pathAntlers, int p, int k, Graph rest, double r, double t) {
        System.out.print("Start combining\r");
        int n = rest.n;
        int m = pathAntlers.length;
        int edgeLength = 0;
        int[] e1 = new int[k*n*m+k*k*m*m+2*m];
        int[] e2 = new int[k*n*m+k*k*m*m+2*m];
        Random rand = new Random();
        for(int l = 0; l < m; l++) {
            for(int i = p*(l+1)+k*l; i < (p+k)*(l+1); i++) for(int j = p*m+k*m; j < p*m+k*m+n; j++) if(rand.nextDouble() < r) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
        }
        for(int l1 = 0; l1 < m; l1++) {
            for(int l2 = l1+1; l2 < m; l2++) {
                for(int i = p*(l1+1)+k*l1; i < (p+k)*(l1+1); i++) for(int j = p*(l2+1)+k*l2; j < (p+k)*(l2+1); j++) if(rand.nextDouble() < r) {
                    e1[edgeLength] = i;
                    e2[edgeLength++] = j;
                }
            }
        }
        for(int l = 0; l < m; l++) {
            if(rand.nextDouble() < t) {
                int restNode = rand.nextInt(n)+p*m+k*m;
                e1[edgeLength] = (p+k)*l;
                e2[edgeLength++] = restNode;
            }
            if(rand.nextDouble() < t) {
                int restNode = rand.nextInt(n)+p*m+k*m;
                e1[edgeLength] = (p+k)*l+p-1;
                e2[edgeLength++] = restNode;
            }
        }
        Graph[] toConnect = new Graph[m+1];
        for(int i = 0; i < m; i++) toConnect[i] = pathAntlers[i];
        toConnect[m] = rest;
        e1 = Arrays.copyOf(e1, edgeLength);
        e2 = Arrays.copyOf(e2, edgeLength);
        return connectGraphs(toConnect, e1, e2);
    }

    public static Graph randomSingleTreeAntlerGraph(int f, int k, int n, int m, double fk, double kk, double r, double t) {
        Graph[] trees = new Graph[m];
        Graph[] heads = new Graph[m];
        for(int i = 0; i < m; i++) {
            trees[i] = randomTree(f);
            heads[i] = randomGraph(k, kk);
        }
        Graph rest = randomGraph(n, r);
        Graph[] antlers = new Graph[m];
        Random rand = new Random();
        for(int l = 0; l < m; l++) {
            int edgeLength = 0;
            int[] e1 = new int[f*k];
            int[] e2 = new int[f*k];
            for(int i = 0; i < f; i++) for(int j = f; j < f+k; j++) if(rand.nextDouble() < fk) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            e1 = Arrays.copyOf(e1, edgeLength);
            e2 = Arrays.copyOf(e2, edgeLength);
            antlers[l] = connectGraphs(new Graph[]{trees[l], heads[l]}, e1, e2);
            System.out.print(("Done with m: "+l)+"\r");
        }
        return randomAntlerGraph(antlers, f, k, rest, r, t);
    }

    public static Graph randomSingleTreeAntlerGraph(int f, int k, int n, int m, int a, double r, double t) {
        Graph[] trees = new Graph[m];
        Graph[] heads = new Graph[m];
        for(int i = 0; i < m; i++) {
            trees[i] = randomTree(f);
            heads[i] = new Graph(new int[k][0]);
        }
        Graph rest = randomGraph(n, r);
        Graph[] antlers = new Graph[m];
        int[] headPart = new int[k];
        for(int i = 0; i < k; i++) headPart[i] = i+f;
        for(int l = 0; l < m; l++) {
            int edgeLength = 0;
            List<Integer> order = new ArrayList<>();
            for(int i = 0; i < f*k+k*(k-1)/2; i++) order.add(i);
            Collections.shuffle(order);
            int[] e1 = new int[f*k+k*(k-1)/2];
            int[] e2 = new int[f*k+k*(k-1)/2];
            for(int i = 0; i < f; i++) for(int j = f; j < f+k; j++) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            for(int i = f; i < f+k; i++) for(int j = i+1; j < f+k; j++) {
                e1[edgeLength] = i;
                e2[edgeLength++] = j;
            }
            int[] e1new = new int[e1.length];
            int[] e2new = new int[e2.length];
            for(int i = 0; i < e1.length; i++) e1new[order.get(i)] = e1[i];
            for(int i = 0; i < e2.length; i++) e2new[order.get(i)] = e2[i];
            e1 = e1new;
            e2 = e2new;
            int x = 0;
            int y = order.size();
            Graph antler = null;
            while(y-x > 1) {
                int mid = (x+y)/2;
                antler = connectGraphs(new Graph[]{trees[l], heads[l]}, Arrays.copyOf(e1, mid), Arrays.copyOf(e2, mid));
                FVC fvc = new FVC(antler, headPart);
                fvc.computeMaxA();
                if(fvc.getA().length >= a) {
                    y = mid;
                } else {
                    x = mid;
                }
            }
            antlers[l] = connectGraphs(new Graph[]{trees[l], heads[l]}, Arrays.copyOf(e1, y), Arrays.copyOf(e2, y));
            System.out.print(("Done with m: "+l)+"\r");
        }
        return randomAntlerGraph(antlers, f, k, rest, r, t);
    }

    private static boolean isAcyclic_dfs(int v, int p, boolean[] visited, Graph graph) {
        if(graph.hasEdge(v, v) >= 1) return false;
        for(int i : graph.adj[v]) {
            if(i == p) continue;
            if(visited[i] || (graph.hasEdge(i, v) >= 2)) return false;
            visited[i] = true;
            if(!isAcyclic_dfs(i, v, visited, graph)) return false;
        }
        return true;
    }

    public static boolean isAcyclic(Graph graph) {
        boolean[] visited = new boolean[graph.n];
        for(int i = 0; i < graph.n; i++) {
            if(visited[i]) continue;
            if(!isAcyclic_dfs(i, -1, visited, graph)) return false;
        }
        return true;
    }

    public static Graph subGraph(int[] nodes, Graph graph) {
        int[] inNodes = new int[graph.n];
        int maxcount = 0;
        for(int i = 0; i < nodes.length; i++) inNodes[nodes[i]] = i+1;
        int[][] adj = new int[nodes.length][0];
        for(int i = 0; i < nodes.length; i++) {
            int count = 0;
            for(int j : graph.adj[nodes[i]]) if(inNodes[j] >= 1) count++;
            adj[i] = new int[count];
            maxcount = Math.max(maxcount, count);
            int index = 0;
            for(int j : graph.adj[nodes[i]]) if(inNodes[j] >= 1) adj[i][index++] = inNodes[j]-1;
            Arrays.sort(adj[i]);
        }
        if(maxcount > nodes.length) {
            adj = Arrays.copyOf(adj, maxcount);
            for(int i = nodes.length; i < maxcount; i++) adj[i] = new int[0];
        }
        return new Graph(adj);
    }

    public static int[] getF(int[] C, Graph graph) {
        int[] handled = new int[graph.n];
        for(int i : C) handled[i] = 2;
        int[] queue = new int[graph.n+2*graph.m()];
        int queueSize = 0;
        for(int i = 0; i < graph.n; i++) {
            if(graph.adj[i].length >= 0) queue[queueSize++] = i;
        }
        int index = 0;
        while(index < queueSize) {
            int node = queue[index];
            index++;
            if(handled[node] > 0) continue;
            int outside = 0;
            int neighbor = -1;
            for(int i : graph.adj[node]) {
                if(handled[i] == 0) {
                    outside++;
                    neighbor = i;
                }
            }
            if(outside <= 1) handled[node] = 1;
            if(outside <= 1 && neighbor != -1) queue[queueSize++] = neighbor;
        }
        int count = 0;
        for(int i = 0; i < graph.n; i++) if(handled[i] == 1) count++;
        int[] F = new int[count];
        index = 0;
        for(int i = 0; i < graph.n; i++) if(handled[i] == 1) F[index++] = i;
        return F;
    }

    public static int maxDiameterForest(int[] F, Graph graph) {
        int maxDia = 0;
        boolean[] inF = new boolean[graph.n];
        int[] visited = new int[graph.n];
        for(int v : F) inF[v] = true;
        int[] queue = new int[2*graph.n];
        for(int v : F) {
            int queueSize = 0;
            int index = 0;
            if(visited[v] > 0) continue;
            queue[queueSize++] = v;
            queue[queueSize++] = 0;
            visited[v] = 1;
            while(index < queueSize) {
                int u = queue[index++];
                int dist = queue[index++];
                for(int w : graph.adj[u]) {
                    if(visited[w] > 0 || !inF[w]) continue;
                    visited[w] = 1;
                    queue[queueSize++] = w;
                    queue[queueSize++] = dist+1;
                }
            }
            int furthestV = queue[queueSize-2];
            queueSize = 0;
            index = 0;
            queue[queueSize++] = furthestV;
            queue[queueSize++] = 0;
            visited[furthestV] = 2;
            while(index < queueSize) {
                int u = queue[index++];
                int dist = queue[index++];
                for(int w : graph.adj[u]) {
                    if(visited[w] > 1 || !inF[w]) continue;
                    visited[w] = 2;
                    queue[queueSize++] = w;
                    queue[queueSize++] = dist+1;
                }
            }
            maxDia = Math.max(maxDia, queue[queueSize-1]);
        }
        return maxDia;
    }

    public static PathAntler[] getSingletonPathAntlers(Graph graph) {
        int[][] pathnodes = new int[graph.n][0];
        int sizeP = 0;
        for(int i = 0; i < graph.n; i++) {
            if(graph.adj[i].length <= 4) {
                int[] nbh = new int[4];
                int nbhSize = graph.N(i, nbh);
                if(nbhSize <= 3) {
                    for(int j = 0; j < nbhSize; j++) {
                        if(graph.used[nbh[j]] != 'F' && graph.adj[i].length-graph.hasEdge(i, nbh[j]) == nbhSize-1) {
                            int[] a = new int[pathnodes[nbh[j]].length+1];
                            System.arraycopy(pathnodes[nbh[j]], 0, a, 0, pathnodes[nbh[j]].length);
                            a[a.length-1] = i;
                            pathnodes[nbh[j]] = a;
                            sizeP++;
                        }
                    }
                }
            }
        }
        PathAntler[] pathAntlers = new PathAntler[sizeP];
        int length = 0;
        for(int i = 0; i < graph.n; i++) {
            boolean[] visited = new boolean[pathnodes[i].length];
            int[] queue = new int[pathnodes[i].length];
            int start = 0;
            int index = 0;
            int end = 0;
            for(int j = 0; j < pathnodes[i].length; j++) {
                if(visited[j]) continue;
                visited[j] = true;
                start = index;
                queue[end++] = pathnodes[i][j];
                while(index < end) {
                    int node = queue[index++];
                    for(int nb : graph.adj[node]) {
                        int loc = Arrays.binarySearch(pathnodes[i], nb);
                        if(loc < 0 || visited[loc]) continue;
                        visited[loc] = true;
                        queue[end++] = nb;
                    }
                }
                int[] p = new int[end-start];
                System.arraycopy(queue, start, p, 0, end-start);
                Arrays.sort(p);
                PathAntler pa = new PathAntler(graph, new int[0], new int[]{i}, p);
                boolean nonEmptyA = (p.length >= 4);
                nonEmptyA = (nonEmptyA || (p.length >= 3 && pa.isCyclic));
                nonEmptyA = (nonEmptyA || (p.length >= 3 && graph.hasEdge(i, pa.endpoints[0]) >= 2));
                nonEmptyA = (nonEmptyA || (p.length >= 3 && graph.hasEdge(i, pa.endpoints[1]) >= 2));
                nonEmptyA = (nonEmptyA || (p.length >= 2 && graph.hasEdge(i, pa.endpoints[0]) >= 2 && graph.hasEdge(i, pa.endpoints[1]) >= 2));
                nonEmptyA = (nonEmptyA || graph.hasEdge(i, i) >= 1);
                if(nonEmptyA) {
                    pa.addA(i);
                }
                pathAntlers[length++] = pa;
            }
        }
        return Arrays.copyOf(pathAntlers, length);
    }

    public static PathAntler[] getKPathAntlers(int k, Graph graph, boolean onlyLengthCheck) {
        return getKPathAntlers(k, graph, onlyLengthCheck, Integer.MAX_VALUE);
    }

    public static PathAntler[] getKPathAntlers(int k, Graph graph, boolean onlyLengthCheck, int maxNumber) {
        if(k == 1) {
            PathAntler[] singlePathAntlers = getSingletonPathAntlers(graph);
            PathAntler[] nonEmptyPathAntlers = new PathAntler[singlePathAntlers.length];
            int paLength = 0;
            for(PathAntler pathAntler : singlePathAntlers) {
                if(pathAntler.getA().length >= 1) {
                    nonEmptyPathAntlers[paLength++] = pathAntler;
                }
            }
            if(paLength >= 1) {
                return Arrays.copyOf(nonEmptyPathAntlers, paLength);
            } else {
                if(singlePathAntlers.length > maxNumber) {
                    Arrays.sort(singlePathAntlers, new Comparator<PathAntler>() {
                        @Override
                        public int compare(PathAntler p1, PathAntler p2) {
                            if(p1.aCount == p2.aCount) return p2.getP().length-p1.getP().length;
                            return p2.aCount-p1.aCount;
                        }
                    });
                    singlePathAntlers = Arrays.copyOf(singlePathAntlers, maxNumber);
                }
                return singlePathAntlers;
            }
        }
        PathAntler[] prevPathAntlers = getKPathAntlers(k-1, graph, onlyLengthCheck, maxNumber);
        if(Thread.currentThread().isInterrupted()) return new PathAntler[0];
        if(prevPathAntlers.length >= 1 && prevPathAntlers[0].getA().length >= 1) {
            return prevPathAntlers;
        }
        PathAntler[] nextPathAntlers = new PathAntler[prevPathAntlers.length+10];
        int paLength = 0;
        for(PathAntler pathAntler : prevPathAntlers) {
            for(int i = 0; i < 2; i++) {
                int nextNode = pathAntler.nextnodes[i];
                if(nextNode == -1 || pathAntler.extended[i] ||
                    graph.hasEdge(nextNode, nextNode) >= 1 ||
                    graph.used[nextNode] == 'F') {
                    pathAntler.extended[i] = true;
                    continue;
                }
                if(graph.adj[nextNode].length >= 2*k+2) continue;
                int nbInF = -1;
                boolean possible = true;
                int[] nbhNodes = new int[graph.adj[nextNode].length];
                int nbh = 0;
                for (int j = 0; j < graph.adj[nextNode].length; j++) {
                    int v = graph.adj[nextNode][j];
                    if(Arrays.binarySearch(pathAntler.getC(), v) >= 0) continue;
                    if(Arrays.binarySearch(pathAntler.getP(), v) >= 0) continue;
                    nbhNodes[nbh++] = v;
                    if(graph.used[v] == 'F' && nbInF == -1) {
                        nbInF = v;
                        if(graph.hasEdge(nextNode, v) >= 2) {
                            possible = false;
                            break;
                        }
                        nbh--;
                    } else if(graph.used[v] == 'F') {
                        possible = false;
                        break;
                    }
                    if (j + 1 < graph.adj[nextNode].length && graph.adj[nextNode][j + 1] == v) j++;
                }
                if(!possible) {
                    pathAntler.extended[i] = true;
                    continue;
                }
                nbhNodes = Arrays.copyOf(nbhNodes, nbh);
                if(nbInF != -1 && nbh + pathAntler.getC().length == k) {
                    PathAntler newPathAntler = new PathAntler(pathAntler);
                    pathAntler.extended[i] = true;
                    newPathAntler.addC(nbhNodes);
                    if(Arrays.binarySearch(newPathAntler.getC(), pathAntler.nextnodes[0]) >= 0)
                        newPathAntler.nextnodes[0] = -1;
                    if(Arrays.binarySearch(newPathAntler.getC(), pathAntler.nextnodes[1]) >= 0)
                        newPathAntler.nextnodes[1] = -1;
                    newPathAntler.extendP(true);
                    if(paLength >= nextPathAntlers.length)
                        nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                    nextPathAntlers[paLength++] = newPathAntler;
                    continue;
                } else if(nbInF == -1 && nbh + pathAntler.getC().length == k+1) {
                    boolean hasExtended = false;
                    for(int j = 0; j < nbhNodes.length; j++) {
                        int v = nbhNodes[j];
                        if(graph.hasEdge(v, nextNode) >= 2) continue;
                        PathAntler newPathAntler = new PathAntler(pathAntler);
                        hasExtended = true;
                        int[] toAdd = new int[nbhNodes.length-1];
                        System.arraycopy(nbhNodes, 0, toAdd, 0, j);
                        System.arraycopy(nbhNodes, j+1, toAdd, j, nbhNodes.length-j-1);
                        newPathAntler.addC(toAdd);
                        if(Arrays.binarySearch(newPathAntler.getC(), pathAntler.nextnodes[0]) >= 0)
                            newPathAntler.nextnodes[0] = -1;
                        if(Arrays.binarySearch(newPathAntler.getC(), pathAntler.nextnodes[1]) >= 0)
                            newPathAntler.nextnodes[1] = -1;
                        newPathAntler.extendP(true);
                        if(paLength >= nextPathAntlers.length)
                            nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                        nextPathAntlers[paLength++] = newPathAntler;
                    }
                    if(hasExtended) pathAntler.extended[i] = true;
                } else if(nbInF == -1 && nbh + pathAntler.getC().length == k) {
                    PathAntler newPathAntler = new PathAntler(pathAntler);
                    pathAntler.extended[i] = true;
                    newPathAntler.addC(nbhNodes);
                    if(Arrays.binarySearch(newPathAntler.getC(), pathAntler.nextnodes[0]) >= 0)
                        newPathAntler.nextnodes[0] = -1;
                    if(Arrays.binarySearch(newPathAntler.getC(), pathAntler.nextnodes[1]) >= 0)
                        newPathAntler.nextnodes[1] = -1;
                    newPathAntler.extendP(true);
                    if(paLength >= nextPathAntlers.length)
                        nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                    nextPathAntlers[paLength++] = newPathAntler;
                }
            }
            if((!pathAntler.extended[0] && pathAntler.nextnodes[0] != -1) || (!pathAntler.extended[1] && pathAntler.nextnodes[1] != -1)) {
                if(paLength >= nextPathAntlers.length)
                    nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                nextPathAntlers[paLength++] = pathAntler;
            }
        }
        Map<Integer, Integer> hashTable = new HashMap<>();
        Map<Integer, int[]> hashToC = new HashMap<>();
        Map<Integer, int[]> hashToP = new HashMap<>();
        for(int v = 0; v < graph.n; v++) {
            if(graph.adj[v].length <= 2*k+2) {
                int[] nbh = new int[graph.adj[v].length];
                int nbhSize = graph.N(v, nbh);
                nbh = Arrays.copyOf(nbh, nbhSize);
                if(nbhSize >= k && nbhSize <= k+2) {
                    for(int i = nbhSize >= k+1 ? 0 : -2; i < (nbhSize >= k+1 ? nbhSize : -1); i++) {
                        for(int j = nbhSize >= k+2 ? i+1 : -2; j < (nbhSize >= k+2 ? nbhSize : -1); j++) {
                            if(nbhSize >= k+1 && graph.hasEdge(nbh[i], v) >= 2) continue;
                            if(nbhSize >= k+2 && graph.hasEdge(nbh[j], v) >= 2) continue;
                            int hash = 0;
                            boolean possible = true;
                            int[] nbhC = new int[k];
                            int index = 0;
                            for(int w : nbh) {
                                if((nbhSize >= k+1 && w == nbh[i]) || (nbhSize >= k+2 && w == nbh[j])) continue;
                                if(graph.used[w] == 'F') {
                                    possible = false;
                                    break;
                                }
                                if(!hashTable.containsKey(hash*graph.n+w)) {
                                    hashTable.put(hash*graph.n+w, hashTable.size()+1);
                                }
                                hash = hashTable.get(hash*graph.n+w);
                                nbhC[index++] = w;
                            }
                            if(possible) {
                                if(!hashToC.containsKey(hash)) hashToC.put(hash, nbhC);
                                if(!hashToP.containsKey(hash)) hashToP.put(hash, new int[0]);
                                int[] p = hashToP.get(hash);
                                int[] newP = new int[p.length+1];
                                System.arraycopy(p, 0, newP, 0, p.length);
                                newP[newP.length-1] = v;
                                hashToP.put(hash, newP);
                            }
                        }
                    }
                    
                }
            }
        }
        for(int hash : hashToP.keySet()) {
            int[] pathnodes = hashToP.get(hash);
            int[] headnodes = hashToC.get(hash);
            boolean[] visited = new boolean[pathnodes.length];
            int[] queue = new int[pathnodes.length];
            int start = 0;
            int index = 0;
            int end = 0;
            for(int i = 0; i < pathnodes.length; i++) {
                if(visited[i]) continue;
                visited[i] = true;
                start = index;
                queue[end++] = pathnodes[i];
                while(index < end) {
                    int node = queue[index++];
                    for(int nb : graph.adj[node]) {
                        int loc = Arrays.binarySearch(pathnodes, nb);
                        if(loc < 0 || visited[loc]) continue;
                        visited[loc] = true;
                        queue[end++] = nb;
                    }
                }
                int[] p = new int[end-start];
                System.arraycopy(queue, start, p, 0, end-start);
                Arrays.sort(p);
                PathAntler pathAntler = new PathAntler(graph, new int[0], headnodes, p);
                pathAntler.computeStatistics();
                int size = pathAntler.getP().length;
                pathAntler.extendP(true);
                if(pathAntler.getP().length != size) continue;
                if(paLength >= nextPathAntlers.length)
                    nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                nextPathAntlers[paLength++] = pathAntler;
            }
        }
        nextPathAntlers = Arrays.copyOf(nextPathAntlers, paLength);
        PathAntler[] uniquePathAntlers = new PathAntler[paLength];
        int paLength2 = 0;
        Arrays.sort(nextPathAntlers, new Comparator<PathAntler>() {
            @Override
            public int compare(PathAntler p1, PathAntler p2) {
                if(p1.getC().length != p2.getC().length) return p1.getC().length-p2.getC().length;
                if(p1.getP().length != p2.getP().length) return p1.getP().length-p2.getP().length;
                for(int i = 0; i < p1.getC().length; i++) {
                    if(p1.getC()[i] != p2.getC()[i]) return p1.getC()[i]-p2.getC()[i];
                }
                for(int i = 0; i < p1.getP().length; i++) {
                    if(p1.getP()[i] != p2.getP()[i]) return p1.getP()[i]-p2.getP()[i];
                }
                return 0;
            }
        });
        for(int i = 0; i < nextPathAntlers.length; i++) {
            if(i == 0 || !nextPathAntlers[i].equals(nextPathAntlers[i-1])) {
                uniquePathAntlers[paLength2++] = nextPathAntlers[i];
            } else if(i > 0) {
                uniquePathAntlers[paLength2-1].extended[0] = uniquePathAntlers[paLength2-1].extended[0] && nextPathAntlers[i].extended[0];
                uniquePathAntlers[paLength2-1].extended[1] = uniquePathAntlers[paLength2-1].extended[1] && nextPathAntlers[i].extended[1];
            }
        }
        uniquePathAntlers = Arrays.copyOf(uniquePathAntlers, paLength2);
        PathAntler[] nonEmptyPathAntlers = new PathAntler[nextPathAntlers.length];
        int paLength3 = 0;
        for(PathAntler pathAntler : uniquePathAntlers) {
            pathAntler.computeMaxA(onlyLengthCheck);
            if(pathAntler.getA().length >= 1) {
                nonEmptyPathAntlers[paLength3++] = pathAntler;
            }
        }
        if(paLength3 >= 1) {
            return Arrays.copyOf(nonEmptyPathAntlers, paLength3);
        } else {
            if(uniquePathAntlers.length > maxNumber) {
                Arrays.sort(uniquePathAntlers, new Comparator<PathAntler>() {
                    @Override
                    public int compare(PathAntler p1, PathAntler p2) {
                        if(p1.aCount == p2.aCount) return p2.getP().length-p1.getP().length;
                        return p2.aCount-p1.aCount;
                    }
                });
                uniquePathAntlers = Arrays.copyOf(uniquePathAntlers, maxNumber);
            }
            return uniquePathAntlers;
        }
    }

    // public static List<FVC> find2Antlers(Graph graph, boolean checkF) {
    //     // TODO finish this with actual path antler 1 handling instead of 2
    //     List<FVC> fvcList = new ArrayList<>();
    //     List<PathAntler> singletonPathAntlers = getSingletonPathAntlers(graph, checkF);
    //     for(PathAntler pathAntler : singletonPathAntlers) {
    //         if(pathAntler.isCyclic) {
    //             fvcList.add(new FVC(graph, new HashSet<>(Arrays.asList(new Node[]{pathAntler.getC().iterator().next(), pathAntler.getP().iterator().next()}))));

    //         }
    //     }
    //     return null;
    // }

    private static int[][] extendC(int i, int[] c, int j, int k, Graph graph) {
        if(Thread.currentThread().isInterrupted()) return new int[0][0];
        if(j == k) {
            return new int[][]{c.clone()};
        }
        int[][] extensions = new int[0][0];
        int length = 0;
        for(int next = i; next < graph.n; next++) {
            if(graph.used[next] == 0 && graph.adj[next].length >= 3 && Arrays.binarySearch(c, next) < 0) {
                int temp = j;
                while(temp >= 1 && c[temp-1] > next) {
                    c[temp] = c[temp-1];
                    temp--;
                }
                c[temp] = next;
                int[][] extension = extendC(next+1, c, j+1, k, graph);
                for(int reset = temp; reset < j; reset++) {
                    c[reset] = c[reset+1];
                }
                if(length+extension.length >= extensions.length)
                    extensions = Arrays.copyOf(extensions, (int)Math.round(1.5*(length+extension.length)));
                for(int[] extend : extension) extensions[length++] = extend;
            }
        }
        return Arrays.copyOf(extensions, length);
    }

    public static FVC[] getKAntlers(int k, Graph graph, boolean onlyFlower) {
        // Assumes that no path-antlers of size k exist in the graph for efficiency guarentee
        int[][] Cs = new int[(k+2)*graph.n][];
        int length = 0;
        for(int v = 0; v < graph.n; v++) {
            if(Thread.currentThread().isInterrupted()) return new FVC[0];
            if(graph.adj[v].length >= 3 && graph.adj[v].length <= 2*k+1) {
                int[] nbh = new int[graph.adj[v].length];
                int nbhSize = graph.N(v, nbh);
                nbh = Arrays.copyOf(nbh, nbhSize);
                int nbInF = -1;
                boolean possible = true;
                for(int w : nbh) {
                    if(graph.used[w] == 'F' && nbInF == -1) {
                        nbInF = w;
                        if(graph.hasEdge(v, w) >= 2) {
                            possible = false;
                            break;
                        }
                    }
                    if(graph.used[w] == 'F') {
                        possible = false;
                        break;
                    }
                }
                if(!possible) continue;
                if(nbhSize <= k+1) {
                    if(nbInF != -1) {
                        int[] c = new int[nbhSize-1];
                        int index = 0;
                        for(int w : nbh) {
                            if(w == nbInF) continue;
                            c[index++] = w;
                        }
                        Cs[length++] = c;
                    } else {
                        for(int u : nbh) {
                            if(graph.hasEdge(v, u) >= 2) continue;
                            int[] c = new int[nbhSize-1];
                            int index = 0;
                            for(int w : nbh) {
                                if(w == u) continue;
                                c[index++] = w;
                            }
                            Cs[length++] = c;
                        }
                    }
                }
                if(nbhSize <= k && nbInF == -1) {
                    Cs[length++] = nbh;
                }
            }
        }
        Cs = Arrays.copyOf(Cs, length);
        Arrays.sort(Cs, (a, b) -> {
            if(a.length != b.length) return a.length - b.length;
            for(int i = 0; i < a.length; i++) {
                if(a[i] != b[i]) return a[i] - b[i];
            }
            return 0;
        });
        int[][] tempCs = new int[Cs.length][0];
        length = 0;
        for(int i = 0; i < Cs.length; i++) {
            tempCs[length++] = Cs[i];
            while(true) {
                if(i+1 >= Cs.length) break;
                if(Arrays.equals(Cs[i], Cs[i+1])) i++;
                else break;
            }
        }
        Cs = Arrays.copyOf(tempCs, length);
        int[][] ExtendedCs = new int[0][0];
        length = 0;
        for(int[] c : Cs) {
            if(Thread.currentThread().isInterrupted()) return new FVC[0];
            int[][] extension = extendC(0, Arrays.copyOf(c, k), c.length, k, graph);
            if(length+extension.length >= ExtendedCs.length)
                ExtendedCs = Arrays.copyOf(ExtendedCs, (int)Math.round(1.5*(length+extension.length)));
            for(int[] extend : extension) ExtendedCs[length++] = extend;
        }
        ExtendedCs = Arrays.copyOf(ExtendedCs, length);
        Arrays.sort(ExtendedCs, (a, b) -> {
            if(a.length != b.length) return a.length - b.length;
            for(int i = 0; i < a.length; i++) {
                if(a[i] != b[i]) return a[i] - b[i];
            }
            return 0;
        });
        int[][] tempExtendedCs = new int[ExtendedCs.length][0];
        length = 0;
        for(int i = 0; i < ExtendedCs.length; i++) {
            if(Thread.currentThread().isInterrupted()) return new FVC[0];
            tempExtendedCs[length++] = ExtendedCs[i];
            while(true) {
                if(i+1 >= ExtendedCs.length) break;
                if(Arrays.equals(ExtendedCs[i], ExtendedCs[i+1])) i++;
                else break;
            }
        }
        ExtendedCs = Arrays.copyOf(tempExtendedCs, length);
        FVC[] nonEmptyFVC = new FVC[10];
        length = 0;
        for(int[] c : ExtendedCs) {
            if(Thread.currentThread().isInterrupted()) return new FVC[0];
            FVC fvc = new FVC(graph, c);
            fvc.computeMaxA(onlyFlower);
            if(fvc.getA().length >= 1) {
                if(length >= nonEmptyFVC.length)
                    nonEmptyFVC = Arrays.copyOf(nonEmptyFVC, (int)Math.round(1.5*(length)));
                nonEmptyFVC[length++] = fvc;
            }
        }
        return Arrays.copyOf(nonEmptyFVC, length);
    }

    interface ScoringFunction {
        int getScore(FVC fvc);
    }

    public static FVC getKAntlerHeuristicF(int k, Graph graph, boolean onlyFlower, boolean fromHeuristicSolve) {
        return getKAntlerHeuristic(k, graph, onlyFlower, fromHeuristicSolve, graph.n()-k, (FVC fvc) -> {
            return fvc.getF().length;
        });
    }

    public static FVC getKAntlerHeuristicFlower(int k, Graph graph, boolean onlyFlower, boolean fromHeuristicSolve) {
        return getKAntlerHeuristic(k, graph, onlyFlower, fromHeuristicSolve, k*k, (FVC fvc) -> {
            int score = 0;
            for(int v : fvc.getC()) {
                int flower = GraphAlgorithm.hasFlower(fvc.getF(), v, fvc.graph);
                if(flower >= fvc.getC().length) flower *= fvc.getC().length;
                score += flower;
            }
            return score;
        });
    }

    public static FVC getKAntlerHeuristicEdge(int k, Graph graph, boolean onlyFlower, boolean fromHeuristicSolve) {
        return getKAntlerHeuristic(k, graph, onlyFlower, fromHeuristicSolve, Integer.MAX_VALUE, (FVC fvc) -> {
            int score = 0;
            for(int v : fvc.getF()) for(int w : fvc.graph.adj[v]) if(fvc.inC(w) || fvc.inF(w)) score++;
            for(int v : fvc.getC()) for(int w : fvc.graph.adj[v]) if(fvc.inC(w) || fvc.inF(w)) score++;
            return score / 2;
        });
    }

    public static FVC getKAntlerHeuristicDiameter(int k, Graph graph, boolean onlyFlower, boolean fromHeuristicSolve) {
        return getKAntlerHeuristic(k, graph, onlyFlower, fromHeuristicSolve, k*(2*k+1)+1, (FVC fvc) -> {
            return GraphAlgorithm.maxDiameterForest(fvc.getF(), fvc.graph);
        });
    }

    public static FVC getKAntlerHeuristic(int k, Graph graph, boolean onlyFlower, boolean fromHeuristicSolve, int earlyReturnScore, ScoringFunction score) {
        int[] nodes = new int[graph.n()];
        int index = 0;
        for(int i = 0; i < graph.n; i++) if(graph.adj[i].length > 0) nodes[index++] = i;
        if(fromHeuristicSolve) nodes = heuristicFVS(graph);
        if(k >= nodes.length) return new FVC(graph);
        int[] swapnodes = new int[nodes.length-k];
        int[] c = new int[k];
        index = 0;
        Random rand = new Random();
        for(int i = 0; i < k; i++) {
            int toAdd = nodes[rand.nextInt(nodes.length)];
            boolean unique = true;
            for(int j = 0; j < index; j++) if(c[j] == toAdd) unique = false;
            if(unique) c[index++] = toAdd;
            else i--;
        }
        index = 0;
        for(int i = 0; i < nodes.length; i++) {
            boolean notInC = true;
            for(int j = 0; j < k; j++) if(c[j] == nodes[i]) notInC = false;
            if(notInC) swapnodes[index++] = nodes[i];
        }
        FVC fvc = new FVC(graph, c);
        double scorechange = 0;
        for(int i = 0; i < 100; i++) {
            int prevscore = score.getScore(fvc);
            int a = fvc.getC()[rand.nextInt(k)];
            int j = rand.nextInt(swapnodes.length);
            int b = swapnodes[j];
            fvc.removeC(a);
            fvc.addC(b);
            fvc.setMaxF();
            swapnodes[j] = a;
            int nextscore = score.getScore(fvc);
            scorechange += Math.abs(prevscore-nextscore);
        }
        scorechange /= 100;
        double temp = -scorechange/Math.log(0.5);
        double endTemp = (-1/(-9*Math.log(10)));
        double reduce = 0.999;
        int prevscore = score.getScore(fvc);
        FVC best = new FVC(graph, fvc.getC());
        int bestscore = prevscore;
        while(temp >= endTemp) {
            int a = fvc.getC()[rand.nextInt(k)];
            int j = rand.nextInt(swapnodes.length);
            int b = swapnodes[j];
            fvc.removeC(a);
            fvc.addC(b);
            fvc.setMaxF();
            int nextscore = score.getScore(fvc);
            if(rand.nextDouble() <= Math.exp((nextscore-prevscore)/temp)) {
                swapnodes[j] = a;
                prevscore = nextscore;
                if(bestscore < nextscore) {
                    best = new FVC(graph, fvc.getC());
                    bestscore = nextscore;
                }
            } else {
                fvc.removeC(b);
                fvc.addC(a);
                fvc.setMaxF();
            }
            temp *= reduce;
            if(bestscore >= earlyReturnScore) break;
        }
        best.computeMaxA(onlyFlower);
        return best;
    }

    private static Pair<Integer, Integer> hasFlower_dfs(int r, int v, int[] F, boolean[] visited, Graph graph) {
        int i = Arrays.binarySearch(F, r);
        visited[i] = true;
        int subtreeCycles = 0;
        int extraCycles = 0;
        for(int u : graph.adj[i]) {
            int j = Arrays.binarySearch(F, u);
            if(j < 0 || visited[j]) continue;
            Pair<Integer, Integer> pair = hasFlower_dfs(u, v, F, visited, graph);
            subtreeCycles += pair.a;
            extraCycles += pair.b;
        }
        if(extraCycles + graph.hasEdge(v, r) >= 2) {
            return new Pair<Integer,Integer>(subtreeCycles+1, 0);
        } else if(extraCycles >= 1 || graph.hasEdge(v, r) >= 1) {
            return new Pair<Integer,Integer>(subtreeCycles, 1);
        } else {
            return new Pair<Integer,Integer>(subtreeCycles, 0);
        }
    }

    public static int hasFlower(int[] F, int v, Graph graph) {
        boolean[] visited = new boolean[F.length];
        int treeCycles = 0;
        for(int i = 0; i < F.length; i++) {
            if(visited[i]) continue;
            treeCycles += hasFlower_dfs(F[i], v, F, visited, graph).a;
        }
        return treeCycles;
    }

    public static boolean isFVS(int[] nodes, Graph graph) {
        Graph testGraph = new Graph(graph);
        for(int i : nodes) {
            testGraph.removeV(i);
        }
        return isAcyclic(testGraph);
    }

    private static int[] naiveFVS_dfs(int k, int i, int[] nodes, int[] build, int length, Graph graph) {
        if(length == k) {
            if(isFVS(build, graph)) return build;
            else return null;
        }
        if(i >= nodes.length) return null;
        int[] best1 = naiveFVS_dfs(k, i+1, nodes, build, length, graph);
        if(best1 != null) return best1;
        build[length] = nodes[i];
        int[] best2 = naiveFVS_dfs(k, i+1, nodes, build, length+1, graph);
        if(best2 != null) return best2;
        build[length] = -1;
        return null;
    }

    public static int[] naiveFVS(int k, Graph graph) {
        Graph testGraph = new Graph(graph);
        int[] nodes = new int[testGraph.n];
        for(int i = 0; i < testGraph.n; i++) nodes[i] = i;
        for(int i = 0; i <= k; i++) {
            int[] build = new int[i];
            for(int j = 0; j < i; j++) build[j] = -1;
            int[] fvs = naiveFVS_dfs(i, 0, nodes, build, 0, testGraph);
            if(fvs != null) return fvs;
        }
        return null;
    }

    public static int[] naiveFVS(Graph graph) {
        return naiveFVS(graph.n, graph);
    }

    public static int[] naiveDisjointFVS(int v, int k, Graph graph) {
        Graph testGraph = new Graph(graph);
        int[] nodes = new int[testGraph.n-1];
        for(int i = 0; i < testGraph.n; i++) {
            if(i < v) nodes[i] = i;
            if(i > v) nodes[i-1] = i;
        }
        for(int i = 0; i <= k; i++) {
            int[] build = new int[i];
            for(int j = 0; j < i; j++) build[j] = -1;
            int[] fvs = naiveFVS_dfs(i, 0, nodes, build, 0, testGraph);
            if(fvs != null) return fvs;
        }
        return null;
    }

    public static int[] naiveDisjointFVS(int v, Graph graph) {
        return naiveDisjointFVS(v, graph.n, graph);
    }

    public static int[] smartFVS(int k, Graph graph) {
        Graph testGraph = new Graph(graph);
        fvs_wata_orz.Solver solver = new fvs_wata_orz.FPTBranchingSolver();
        fvs_wata_orz.tc.wata.debug.Debug.silent = true;
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        solver.ub = k+1;
        solver.solve(testGraph);
        return solver.res;
    }

    public static int[] smartFVS(Graph graph) {
        return smartFVS(Integer.MAX_VALUE-1, graph);
    }

    public static int[] smartDisjointFVS(int s, int k, Graph graph) {
        Graph testGraph = new Graph(graph);
        if(testGraph.hasEdge(s, s) >= 1) return null;
        testGraph.setF(s);
        fvs_wata_orz.FPTBranchingSolver solver = new fvs_wata_orz.FPTBranchingSolver();
        fvs_wata_orz.tc.wata.debug.Debug.silent = true;
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        solver.ub = k+1;
        solver.solve(testGraph, s);
        return solver.res;
    }

    public static int[] smartDisjointFVS(int v, Graph graph) {
        return smartDisjointFVS(v, graph.n, graph);
    }

    public static int[] heuristicFVS(Graph graph) {
        Graph testGraph = new Graph(graph);
        while(testGraph.n() > 0) {
            simpleGraphReduction_Heuristic(testGraph);
            if(testGraph.n() == 0) break;
            int s = -1;
            int degree = 0;
            for(int i = 0; i < graph.n; i++) if(degree < testGraph.adj[i].length) {
                s = i;
                degree = testGraph.adj[i].length;
            }
            testGraph.setS(s);
        }
        return testGraph.getS();
    }

    private static void simpleGraphReduction_Heuristic(Graph g) {
        while(true) {
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
                    }
                }
            }
            int newN = g.n();
            if(oldN == newN) break;
        }
    }

    public static Description[] getKSecludedTrees(int k, Graph graph) {
        Description[] allD = new Description[0];
        for(int i = 0; i < graph.n; i++) {
            boolean[] T = new boolean[graph.n];
            boolean[] F = new boolean[graph.n];
            T[i] = true;
            F[i] = true;
            Graph g = new Graph(graph);
            Description[] d = getKSecludedTrees(T, 1, F, 1, k, g);
            allD = Arrays.copyOf(allD, allD.length + d.length);
            System.arraycopy(d, 0, allD, allD.length - d.length, d.length);
        }
        for(Description d : allD) d.setSmallR(graph);
        allD = new HashSet<>(Arrays.asList(allD)).toArray(new Description[0]);
        return allD;
    }

    public static Description[] getKSecludedTrees(boolean[] T, int tsize, boolean[] F, int fsize, int k, Graph g) {
        if(Thread.currentThread().isInterrupted()) return new Description[0];
        // Add neighbor from F that is in graph F to F
        int[] queue = new int[2*g.n];
        int queueSize = 0;
        int index = 0;
        for(int i = 0; i < g.n; i++) if(F[i]) queue[queueSize++] = i;
        while(index < queueSize) {
            int node = queue[index++];
            for(int nb : g.adj[node]) {
                if(g.used[nb] == 'F' && !F[nb]) {
                    F[nb] = true;
                    fsize++;
                    queue[queueSize++] = nb;
                }
            }
        }
        
        // Cycle in F
        boolean[] visited = new boolean[g.n];
        index = 0;
        queueSize = 0;
        for(int i = 0; i < g.n; i++) {
            if(visited[i] || !F[i]) continue;
            visited[i] = true;
            queue[queueSize++] = i;
            queue[queueSize++] = -1;
            while(index < queueSize) {
                int node = queue[index++];
                int prev = queue[index++];
                for(int nb : g.adj[node]) {
                    if(!F[nb]) continue;
                    if(nb == prev) {
                        prev = -1;
                        continue;
                    }
                    if(visited[nb] || nb == node) return new Description[0]; // Cycle in F
                    visited[nb] = true;
                    queue[queueSize++] = nb;
                    queue[queueSize++] = node;
                }
            }
        }

        // Components containing F
        int[] components = new int[g.n];
        int cCount = 1;
        queueSize = 0;
        index = 0;
        for(int i = 0; i < g.n; i++) {
            if(components[i] > 0 || (g.adj[i].length == 0 && !F[i])) continue;
            components[i] = cCount;
            queue[queueSize++] = i;
            while(index < queueSize) {
                int node = queue[index++];
                for(int nb : g.adj[node]) {
                    if(components[nb] > 0) continue;
                    components[nb] = cCount;
                    queue[queueSize++] = nb;
                }
            }
            cCount++;
        }

        int fComponent = 0;
        for(int i = 0; i < g.n; i++) {
            if(F[i] && fComponent > 0 && fComponent != components[i]) return new Description[0]; // F over multiple components
            if(F[i] && fComponent == 0) fComponent = components[i];
        }
        for(int i = 0; i < g.n; i++) if(components[i] != fComponent) g.adj[i] = new int[0];

        // leaf removal
        queueSize = 0;
        index = 0;
        for(int i = 0; i < g.n; i++) if(g.adj[i].length == 1 && g.adj[i][0] != i) queue[queueSize++] = i;
        while(index < queueSize) {
            int node = queue[index++];
            if(tsize == 1 && T[node]) continue;
            if(T[node]) {
                T[node] = false;
                tsize--;
            }
            if(F[node]) {
                F[node] = false;
                fsize--;
            }
            if(g.adj[node].length == 0) continue;
            int nb = g.adj[node][0];
            g.removeV(node);
            if(g.adj[nb].length == 1 && g.adj[nb][0] != nb) queue[queueSize++] = nb;
        }

        // Add neighbor F to T
        index = 0;
        queueSize = 0;
        for(int i = 0; i < g.n; i++) if(T[i]) queue[queueSize++] = i;
        while(index < queueSize) {
            int node = queue[index++];
            for(int nb : g.adj[node]) {
                if(F[nb] && !T[nb]) {
                    T[nb] = true;
                    tsize++;
                    queue[queueSize++] = nb;
                }
            }
        }

        // F no neighbors
        boolean hasNb = false;
        int nodeF = -1;
        for(int i = 0; i < g.n && !hasNb; i++) if(F[i]) {
            nodeF = i;
            for(int j : g.adj[i]) {
                if(!F[j]) {
                    hasNb = true;
                    break;
                }
            }
        }
        if(!hasNb) return new Description[]{new Description(nodeF, new int[0][0])};

        // no k
        if(k == 0) return new Description[0];

        // Node with 2 neighbors in T
        for(int i = 0; i < g.n; i++) {
            if(T[i] || F[i]) continue;
            int tCount = 0;
            for(int j : g.adj[i]) {
                if(T[j]) tCount++;
            }
            if(tCount >= 2) {
                g.removeV(i);
                Description[] desc = getKSecludedTrees(T, tsize, F, fsize, k-1, g); // Had a node with 2 neighbors in T
                if(Thread.currentThread().isInterrupted()) return new Description[0];
                for(Description d : desc) d.addX(new int[]{i});
                return desc;
            }
        }
        
        // Node within border nbh of T
        int nbhT = 0;
        int nbNodeT = -1;
        for(int i = 0; i < g.n; i++) {
            if(T[i]) continue;
            for(int j : g.adj[i]) {
                if(T[j]) {
                    nbhT++;
                    nbNodeT = i;
                    break;
                }
            }
        }
        if(nbhT > k*(k+1)) {
            Graph g1 = new Graph(g);

            // Contract T
            int[] nbhNodesT = new int[g1.n];
            int nodeT = -1;
            for(int i = 0; i < g1.n; i++) {
                if(T[i]) {
                    nodeT = i;
                    continue;
                }
                for(int v : g1.adj[i]) {
                    if(T[v]) {
                        nbhNodesT[i]++;
                        break;
                    }
                }
            }
            int nbhSizeT = 0;
            for(int i = 0; i < g1.n; i++) nbhSizeT += Math.min(nbhNodesT[i], 2);
            int[] newNbh = new int[nbhSizeT];
            index = 0;
            for(int i = 0; i < g1.n; i++) for(int j = 0; j < Math.min(nbhNodesT[i], 2); j++) newNbh[index++] = i;
            g1.adj[nodeT] = newNbh;
            for(int i = 0; i < g1.n; i++) if(T[i] && i != nodeT) g1.adj[i] = new int[0];

            // Find node v
            for(int i = 0; i < g1.n; i++) {
                if(F[i] || g.used[i] == 'F') continue;
                int[][] adj = new int[2*g1.n][];
                int[][] flow = new int[2*g1.n][];
                for(int j = 0; j < g1.n; j++) flow[j] = new int[g1.adj[j].length+1];
                for(int j = 0; j < g1.n; j++) flow[j+g1.n] = new int[g1.adj[j].length+1];
                for(int j = 0; j < g1.n; j++) {
                    adj[j] = new int[g1.adj[j].length+1];
                    System.arraycopy(g1.adj[j], 0, adj[j], 0, g1.adj[j].length);
                    adj[j][g1.adj[j].length] = j+g1.n;
                    for(int l = 0; l < g1.adj[j].length; l++) flow[j][l] = 1;
                }
                for(int j = 0; j < g1.n; j++) {
                    adj[j+g1.n] = new int[g1.adj[j].length+1];
                    System.arraycopy(g1.adj[j], 0, adj[j+g1.n], 0, g1.adj[j].length);
                    adj[j+g1.n][g1.adj[j].length] = j;
                    flow[j+g1.n][g1.adj[j].length] = 1;
                }
                boolean kPathFound = true;
                for(int j = 0; j < k+2; j++) {
                    if(!flow(adj, i+g1.n, nodeT, flow)) {
                        kPathFound = false;
                        break;
                    }
                }
                if(kPathFound) {
                    g.removeV(i);
                    Description[] desc = getKSecludedTrees(T, tsize, F, fsize, k-1, g); // Had a node with k+2 neighbors in T
                    if(Thread.currentThread().isInterrupted()) return new Description[0];
                    for(Description d : desc) d.addX(new int[]{i});
                    return desc;
                }
            }

            return new Description[0]; // No node with k+2 paths to T
        }

        // Splitting on extending path from nbNodeT
        int[] path = new int[g.n];
        boolean[] inPath = new boolean[g.n];
        path[0] = nbNodeT;
        inPath[nbNodeT] = true;
        int pathSize = 1;
        while(g.adj[path[pathSize-1]].length == 2) {
            int nb1 = g.adj[path[pathSize-1]][0];
            int nb2 = g.adj[path[pathSize-1]][1];
            int nb = T[nb1] || inPath[nb1] ? nb2 : nb1;
            if(T[nb] || inPath[nb]) break;
            path[pathSize++] = nb;
            inPath[nb] = true;
        }
        boolean doubleEnd = false;
        if(g.adj[path[pathSize-1]].length == 3) {
            int x = g.adj[path[pathSize-1]][0];
            int y = g.adj[path[pathSize-1]][1];
            int z = g.adj[path[pathSize-1]][2];
            if((x == y || y == z) && !T[y]) {
                path[pathSize++] = y;
                inPath[y] = true;
                doubleEnd = true;
            }
        }
        
        // D1
        Description[] d1 = new Description[0];
        if(!F[path[pathSize-1]] && g.used[path[pathSize-1]] != 'F') {
            Graph g1 = new Graph(g);
            g1.removeV(path[pathSize-1]);
            d1 = getKSecludedTrees(T.clone(), tsize, F.clone(), fsize, k-1, g1);
            if(Thread.currentThread().isInterrupted()) return new Description[0];
            for(Description d : d1) d.addX(new int[]{path[pathSize-1]});
        }

        // D2
        Description[] d2 = new Description[0];
        boolean inF = true;
        for(int i = 0; i < pathSize-1; i++) if(!F[path[i]]) inF = false;
        if(!inF && (!doubleEnd || (!F[path[pathSize-2]] && g.used[path[pathSize-2]] != 'F'))) {
            Graph g2 = new Graph(g);
            for(int i = 0; i < pathSize-1; i++) g2.removeV(path[i]);
            boolean[] reducedF = new boolean[g.n];
            int reducedFsize = 0;
            for(int i = 0; i < g.n; i++) {
                if(!inPath[i]) {
                    reducedF[i] = F[i];
                    if(F[i]) reducedFsize++;
                }
            }
            reducedF[path[pathSize-1]] = true;
            reducedFsize++;
            d2 = getKSecludedTrees(T.clone(), tsize, reducedF, reducedFsize, k-1, g2);
            if(Thread.currentThread().isInterrupted()) return new Description[0];
            if(doubleEnd) {
                for(Description d : d2) d.addX(new int[]{path[pathSize-2]});
            } else {
                for(Description d : d2) {
                    int[] shorterPath = new int[pathSize];
                    int length = 0;
                    for(int i = 0; i < pathSize-1; i++) if(!F[path[i]] && g.used[path[i]] != 'F') shorterPath[length++] = path[i];
                    shorterPath = Arrays.copyOf(shorterPath, length);
                    Arrays.sort(shorterPath);
                    d.addX(shorterPath);
                }
            }
        }

        // D3
        Description[] d3 = new Description[0];
        boolean acyclicPandF = true;
        visited = new boolean[g.n];
        queueSize = 0;
        index = 0;
        for(int i = 0; i < g.n && acyclicPandF; i++) {
            if(visited[i] || (!F[i] && !inPath[i])) continue;
            visited[i] = true;
            queue[queueSize++] = i;
            queue[queueSize++] = -1;
            while(index < queueSize && acyclicPandF) {
                int node = queue[index++];
                int prev = queue[index++];
                for(int nb : g.adj[node]) {
                    if((!F[nb] && !inPath[nb])) continue;
                    if(nb == prev) {
                        prev = -1;
                        continue;
                    }
                    if(visited[nb] || nb == node) {
                        acyclicPandF = false;
                        break;
                    }
                    visited[nb] = true;
                    queue[queueSize++] = nb;
                    queue[queueSize++] = node;
                }
            }
        }
        if(acyclicPandF) {
            Graph g3 = new Graph(g);
            boolean[] addedF = new boolean[g.n];
            int addedFsize = 0;
            boolean[] addedT = new boolean[g.n];
            int addedTsize = 0;
            for(int i = 0; i < g.n; i++) {
                addedF[i] = F[i];
                if(F[i]) addedFsize++;
                addedT[i] = T[i];
                if(T[i]) addedTsize++;
            }
            for(int i = 0; i < pathSize; i++) {
                if(!addedF[path[i]]) addedFsize++;
                addedF[path[i]] = true;
                if(!addedT[path[i]]) addedTsize++;
                addedT[path[i]] = true;
            }
            d3 = getKSecludedTrees(addedT, addedTsize, addedF, addedFsize, k, g3);
            if(Thread.currentThread().isInterrupted()) return new Description[0];
        }
        Description[] d = new Description[d1.length + d2.length + d3.length];
        System.arraycopy(d1, 0, d, 0, d1.length);
        System.arraycopy(d2, 0, d, d1.length, d2.length);   
        System.arraycopy(d3, 0, d, d1.length+d2.length, d3.length);
        return d;
    }

    private static boolean flow(int[][] adj, int s, int t, int[][] flow) {
        int[] queue = new int[adj.length];
        int[] parent = new int[adj.length];
        boolean[] visited = new boolean[adj.length];
        queue[0] = s;
        visited[s] = true;
        int index = 0;
        int queueSize = 1;
        boolean pathFound = false;
        while(index < queueSize && !pathFound) {
            int node = queue[index++];
            for(int j = 0; j < adj[node].length; j++) {
                if(visited[adj[node][j]]) continue;
                if(flow[node][j] >= 1) continue;
                visited[adj[node][j]] = true;
                queue[queueSize++] = adj[node][j];
                parent[adj[node][j]] = node;
                if(adj[node][j] == t) {
                    pathFound = true;
                    break;
                }
            }
            for(int j = 0; j < adj[node].length; j++) {
                if(visited[adj[node][j]]) continue;
                if(flow[node][j] >= 1) continue;
                visited[adj[node][j]] = true;
                queue[queueSize++] = adj[node][j];
                parent[adj[node][j]] = node;
                if(adj[node][j] == t) {
                    pathFound = true;
                    break;
                }
            }
        }
        if(!pathFound) return false;
        int walk = t;
        while(walk != s) {
            int parentNode = parent[walk];
            for(int i = 0; i < adj[parentNode].length; i++) if(adj[parentNode][i] == walk) {
                flow[parentNode][i]++;
                break;
            }
            for(int i = 0; i < adj[walk].length; i++) if(adj[walk][i] == parentNode) {
                flow[walk][i]++;
                break;
            }
            walk = parentNode;
        }
        return true;
    }

    public static FVC[] getSingleTreeAntlers(int k, Graph g) {
        Description[] desc = getKSecludedTrees(k, g);
        if(Thread.currentThread().isInterrupted()) return new FVC[0];
        FVC[] fvcs = new FVC[desc.length*(2*k+1)];
        int fvcLength = 0;
        for(Description d : desc) {
            boolean[] tree = new boolean[g.n];
            int[] inX = new int[g.n];
            int[] xMapi = new int[g.n];
            int[] xMapj = new int[g.n];
            for(int i = 0; i < d.X.length; i++) {
                for(int j = 0; j < d.X[i].length; j++) {
                    xMapi[d.X[i][j]] = i+1;
                    xMapj[d.X[i][j]] = j+1;
                    inX[d.X[i][j]] = d.X[i].length == 1 ? 1 : 2;
                }
            }
            int[] queue = new int[6*g.n];
            queue[0] = d.r;
            queue[1] = 0;
            queue[2] = 0;
            tree[d.r] = true;
            int queueSize = 3;
            int index = 0;
            while(index < queueSize) {
                int node = queue[index++];
                int i = queue[index++];
                int j = queue[index++];
                for(int nb : g.adj[node]) {
                    if(tree[nb] || inX[nb] == 1) continue;
                    if(i == 0 && j == 0) tree[nb] = true;
                    xMapi[nb] = i;
                    xMapj[nb] = j;
                    int nexti = i;
                    int nextj = j;
                    if(inX[nb] > 0) {
                        nexti = xMapi[nb];
                        nextj = xMapi[nb];
                    } else if((xMapi[nb] != 0 || xMapj[nb] != 0) && (xMapi[nb] != i || xMapj[nb] != j)) {
                        nexti = 0;
                        nextj = 0;
                    }
                    queue[queueSize++] = nb;
                    queue[queueSize++] = nexti;
                    queue[queueSize++] = nextj;
                }
            }
            for(int i = 0; i < d.X.length; i++) {
                if(d.X[i].length > 1) continue;
                int v = d.X[i][0];
                int[] nbpointer = new int[2*d.X.length+2];
                int[] nbcounter = new int[2*d.X.length+2];
                int edgesToT = 0;
                for(int nb : g.adj[v]) {
                    if(edgesToT >= 2) break;
                    if(xMapi[nb] == 0 && xMapj[nb] == 0) edgesToT++;
                    else if(nbpointer[2*xMapi[nb]] == xMapj[nb]) nbcounter[2*xMapi[nb]]++;
                    else if(nbpointer[2*xMapi[nb]+1] == xMapj[nb]) nbcounter[2*xMapi[nb]+1]++;
                    else if(nbpointer[2*xMapi[nb]] == 0) {
                        nbpointer[2*xMapi[nb]] = xMapj[nb];
                        nbcounter[2*xMapi[nb]] = 1;
                    } else if(nbpointer[2*xMapi[nb]+1] == 0) {
                        nbpointer[2*xMapi[nb]+1] = xMapj[nb];
                        nbcounter[2*xMapi[nb]+1] = 1;
                    } else {
                        edgesToT = 2;
                        break;
                    }
                }
                for(int j = 0; j < d.X.length; j++) {
                    edgesToT += Math.min(nbcounter[2*j+2], nbcounter[2*j+3]);
                }
                if(edgesToT < 2) {
                    int[] nbh1 = new int[d.X.length-1];
                    int[] nbh2 = new int[d.X.length-1];
                    boolean no2Nbh = true;
                    int length = 0;
                    for(int j = 0; j < d.X.length; j++) {
                        if(i == j) continue;
                        if(d.X[j].length == 1 || (nbcounter[2*j+2] == 0 && nbcounter[2*j+3] == 0)) {
                            nbh1[length] = d.X[j][0];
                            nbh2[length++] = d.X[j][0];
                        } else if(nbcounter[2*j+2] > 0 && nbcounter[2*j+3] == 0) {
                            nbh1[length] = d.X[j][nbpointer[2*j+2]];
                            nbh2[length++] = d.X[j][nbpointer[2*j+2]];
                        } else if(nbcounter[2*j+2] > 0 && nbcounter[2*j+3] > 0) {
                            no2Nbh = false;
                            nbh1[length] = d.X[j][nbpointer[2*j+2]];
                            nbh2[length++] = d.X[j][nbpointer[2*j+3]];
                        }
                    }
                    Arrays.sort(nbh1);
                    Arrays.sort(nbh2);
                    FVC fvc1 = new FVC(g, nbh1);
                    fvc1.computeMaxA();
                    if(fvc1.getA().length > 0) fvcs[fvcLength++] = fvc1;
                    if(!no2Nbh) {
                        FVC fvc2 = new FVC(g, nbh2);
                        fvc2.computeMaxA();
                        if(fvc2.getA().length > 0) fvcs[fvcLength++] = fvc2;
                    }
                }
            }
            int[] nbh = new int[d.X.length];
            for(int i = 0; i < d.X.length; i++) {
                nbh[i] = d.X[i][0];
            }
            FVC fvc = new FVC(g, nbh);
            fvc.computeMaxA();
            if(fvc.getA().length > 0) fvcs[fvcLength++] = fvc;
        }
        return Arrays.copyOf(fvcs, fvcLength);
    }
}
