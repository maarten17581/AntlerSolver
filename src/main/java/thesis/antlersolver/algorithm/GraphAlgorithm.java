package thesis.antlersolver.algorithm;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import fvs_wata_orz.Graph;
import fvs_wata_orz.tc.wata.data.IntArray;
import thesis.antlersolver.model.Description;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;

public class GraphAlgorithm {

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
        int maxdegree = 0;
        for(int i = 0; i < nodes.length; i++) inNodes[nodes[i]] = i+1;
        for(int v : nodes) Math.max(maxdegree, graph.adj[v].length);
        int[][] adj = new int[Math.max(nodes.length, maxdegree)][0];
        for(int i = 0; i < nodes.length; i++) {
            int count = 0;
            for(int j : graph.adj[nodes[i]]) if(inNodes[j] >= 1) count++;
            adj[i] = new int[count];
            int index = 0;
            for(int j : graph.adj[nodes[i]]) if(inNodes[j] >= 1) adj[i][index++] = inNodes[j]-1;
            Arrays.sort(adj[i]);
        }
        return new Graph(adj);
    }

    public static int[] getF(int[] C, Graph graph) {
        int[] handled = new int[graph.n];
        for(int i : C) handled[i] = 2;
        int[] queue = new int[graph.n+2*graph.m()];
        for(int i = 0; i < graph.n; i++) {
            queue[i] = i;
        }
        int queueSize = graph.n;
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
                    Random rand = new Random();
                    for(int i = 0; i < singlePathAntlers.length; i++) {
                        int swapIndex = rand.nextInt(singlePathAntlers.length);
                        PathAntler toSwap = singlePathAntlers[swapIndex];
                        singlePathAntlers[swapIndex] = singlePathAntlers[i];
                        singlePathAntlers[i] = toSwap;
                    }
                    singlePathAntlers = Arrays.copyOf(singlePathAntlers, maxNumber);
                }
                return singlePathAntlers;
            }
        }
        PathAntler[] prevPathAntlers = getKPathAntlers(k-1, graph, onlyLengthCheck, maxNumber);
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
                    pathAntler.extended[i] = true;
                    PathAntler newPathAntler = new PathAntler(pathAntler);
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
                    for(int j = 0; j < nbhNodes.length; j++) {
                        int v = nbhNodes[j];
                        if(graph.hasEdge(v, nextNode) >= 2) continue;
                        pathAntler.extended[i] = true;
                        PathAntler newPathAntler = new PathAntler(pathAntler);
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
                } else if(nbInF == -1 && nbh + pathAntler.getC().length == k) {
                    pathAntler.extended[i] = true;
                    PathAntler newPathAntler = new PathAntler(pathAntler);
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
        nextPathAntlers = new HashSet<>(Arrays.asList(nextPathAntlers)).toArray(new PathAntler[0]);
        PathAntler[] nonEmptyPathAntlers = new PathAntler[nextPathAntlers.length];
        int paLength2 = 0;
        for(PathAntler pathAntler : nextPathAntlers) {
            pathAntler.computeMaxA(onlyLengthCheck);
            if(pathAntler.getA().length >= 1) {
                nonEmptyPathAntlers[paLength2++] = pathAntler;
            }
        }
        if(paLength2 >= 1) {
            return Arrays.copyOf(nonEmptyPathAntlers, paLength2);
        } else {
            if(nextPathAntlers.length > maxNumber) {
                Random rand = new Random();
                for(int i = 0; i < nextPathAntlers.length; i++) {
                    int swapIndex = rand.nextInt(nextPathAntlers.length);
                    PathAntler toSwap = nextPathAntlers[swapIndex];
                    nextPathAntlers[swapIndex] = nextPathAntlers[i];
                    nextPathAntlers[i] = toSwap;
                }
                nextPathAntlers = Arrays.copyOf(nextPathAntlers, maxNumber);
            }
            return nextPathAntlers;
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
