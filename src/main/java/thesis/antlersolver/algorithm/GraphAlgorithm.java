package thesis.antlersolver.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fvs_wata_orz.Graph;
import fvs_wata_orz.tc.wata.data.IntArray;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;
import thesis.antlersolver.statistics.Statistics;

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

    // public static Map<Pair<Node, Integer>, Integer> hashTable = new HashMap<>();
    // public static int maxHash = 1;

    // public static int hashNodes(Set<Node> nodes) {
    //     List<Node> nodeList = new ArrayList<>(nodes);
    //     Collections.sort(nodeList, (n1, n2) -> n1.id-n2.id);
    //     int hash = 0;
    //     for(Node v : nodeList) {
    //         Pair<Node, Integer> nextPair = new Pair<Node,Integer>(v, hash);
    //         if(hashTable.get(nextPair) == null) {
    //             hashTable.put(nextPair, maxHash);
    //             maxHash++;
    //         }
    //         hash = hashTable.get(nextPair);
    //     }
    //     return hash;
    // }

    public static PathAntler[] getKPathAntlers(int k, Graph graph, boolean onlyLengthCheck) {
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
                return singlePathAntlers;
            }
        }
        PathAntler[] prevPathAntlers = getKPathAntlers(k-1, graph, onlyLengthCheck);
        if(prevPathAntlers.length >= 1 && prevPathAntlers[0].getA().length >= 1) {
            return prevPathAntlers;
        }
        PathAntler[] nextPathAntlers = new PathAntler[prevPathAntlers.length];
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
                    newPathAntler.test = "extended1";
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
                        newPathAntler.test = "extended2";
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
                    newPathAntler.test = "extended3";
                }
            }
            if((!pathAntler.extended[0] && pathAntler.nextnodes[0] != -1) || (!pathAntler.extended[1] && pathAntler.nextnodes[1] != -1)) {
                if(paLength >= nextPathAntlers.length)
                    nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                nextPathAntlers[paLength++] = pathAntler;
                pathAntler.test = "not_fully_extended";
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
                PathAntler pathAntler = new PathAntler(graph, new int[0], headnodes, p);
                pathAntler.computeStatistics();
                int size = pathAntler.getP().length;
                pathAntler.extendP(true);
                if(pathAntler.getP().length != size) {
                    continue;
                }
                pathAntler.computeMaxA();
                if(paLength >= nextPathAntlers.length)
                    nextPathAntlers = Arrays.copyOf(nextPathAntlers, (int)Math.round(1.5*paLength));
                nextPathAntlers[paLength++] = pathAntler;
                pathAntler.test = "created";
            }
        }
        nextPathAntlers = Arrays.copyOf(nextPathAntlers, paLength);
        nextPathAntlers = new HashSet<>(Arrays.asList(nextPathAntlers)).toArray(new PathAntler[0]);
        PathAntler[] nonEmptyPathAntlers = new PathAntler[nextPathAntlers.length];
        int paLength2 = 0;
        for(PathAntler pathAntler : nextPathAntlers) {
            if(onlyLengthCheck) {
                for(int c : pathAntler.getC()) {
                    if(hasFlower(pathAntler.getP(), c, graph) >= pathAntler.getC().length+1) {
                        pathAntler.addA(c);
                    }
                }
            } else {
                pathAntler.computeMaxA();
            }
            if(pathAntler.getA().length >= 1) {
                nonEmptyPathAntlers[paLength2++] = pathAntler;
            }
        }
        if(paLength2 >= 1) {
            return Arrays.copyOf(nonEmptyPathAntlers, paLength2);
        } else {
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

    // private static void extendC(Set<Node> c, int i, int k, List<Node> nodes, Set<Set<Node>> extension, boolean checkF) {
    //     if(c.size() == k) {
    //         extension.add(new HashSet<>(c));
    //         return;
    //     }
    //     for(int j = i; j < nodes.size(); j++) {
    //         if((!checkF || !nodes.get(j).isF()) && !c.contains(nodes.get(j))) {
    //             c.add(nodes.get(j));
    //             extendC(c, j+1, k, nodes, extension, checkF);
    //             c.remove(nodes.get(j));
    //         }
    //     }
    // }

    // public static List<FVC> findKAntlers(int k, Graph graph, boolean onlyFlower, boolean checkF) {
    //     // Assumes that no path-antlers of size k exist in the graph for efficiency guarentee
    //     Set<Set<Node>> Cs = new HashSet<>();
    //     for(Node v : graph.nodes.values()) {
    //         if(v.nbhSize <= k+1) {
    //             Node nbInF = null;
    //             if(checkF) {
    //                 boolean possible = true;
    //                 for(Edge e : v.neighbors.values()) {
    //                     if(e.t.isF() && nbInF == null) {
    //                         nbInF = e.t;
    //                         if(e.c >= 2) {
    //                             possible = false;
    //                             break;
    //                         }
    //                     } else if(e.t.isF()) {
    //                         possible = false;
    //                         break;
    //                     }
    //                 }
    //                 if(!possible) {
    //                     continue;
    //                 }
    //             }
    //             if(nbInF != null) {
    //                 Set<Node> c = new HashSet<>(v.neighbors.keySet());
    //                 c.remove(nbInF);
    //                 Cs.add(c);
    //                 continue;
    //             }
    //             for(Edge e : v.neighbors.values()) {
    //                 if(e.c >= 2) continue;
    //                 Set<Node> c = new HashSet<>(v.neighbors.keySet());
    //                 c.remove(e.t);
    //                 Cs.add(c);
    //             }
    //             if(v.nbhSize <= k) {
    //                 Cs.add(new HashSet<>(v.neighbors.keySet()));
    //             }
    //         }
    //     }
    //     Map<Integer, Set<Node>> ExtendedCs = new HashMap<>();
    //     List<Node> nodes = new ArrayList<>(graph.nodes.values());
    //     for(Set<Node> c : Cs) {
    //         Set<Set<Node>> extension = new HashSet<>();
    //         extendC(c, 0, k, nodes, extension, checkF);
    //         for(Set<Node> extendedC : extension) {
    //             ExtendedCs.put(hashNodes(extendedC), extendedC);
    //         }
    //     }
    //     List<FVC> nonEmptyFVC = new ArrayList<>();
    //     for(Set<Node> C : ExtendedCs.values()) {
    //         FVC fvc = new FVC(graph, C);
    //         fvc.computeMaxA(onlyFlower);
    //         if(!fvc.getA().isEmpty()) {
    //             nonEmptyFVC.add(fvc);
    //         }
    //     }
    //     return nonEmptyFVC;
    // }

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
}
