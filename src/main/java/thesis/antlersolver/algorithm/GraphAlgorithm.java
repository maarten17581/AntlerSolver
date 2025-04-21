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

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;
import thesis.antlersolver.statistics.Statistics;

public class GraphAlgorithm {

    public static List<List<Node>> connectedComponents(Graph graph) {
        Set<Node> found = new HashSet<>();
        List<List<Node>> components = new ArrayList<>();
        for(Node v : graph.nodes.values()) {
            if(found.contains(v)) continue;
            List<Node> component = new ArrayList<>();
            component.add(v);
            found.add(v);
            for(int i = 0; i < component.size(); i++) {
                for(Node w : component.get(i).neighbors.keySet()) {
                    if(found.contains(w)) continue;
                    component.add(w);
                    found.add(w);
                }
            }
            components.add(component);
        }
        return components;
    }

    public static List<Graph> connectedComponentsGraph(Graph graph) {
        List<List<Node>> cc = connectedComponents(graph);
        List<Graph> graphs = new ArrayList<>();
        for(int i = 0; i < cc.size(); i++) {
            Graph componentGraph = new Graph(graph.name+":"+i);
            componentGraph.addNodeSet(cc.get(i));
            graphs.add(componentGraph);
        }
        return graphs;
    }

    public static Graph subGraph(List<Node> nodes, Graph graph) {
        Graph subGraph = new Graph("sub-"+graph.name);
        for(Node v : nodes) {
            subGraph.addNode(v.id);
        }
        for(Node v : nodes) {
            for(Edge e : v.neighbors.values()) {
                if(e.t.id > v.id) {
                    subGraph.addEdge(v.id, e.t.id, e.c);
                }
            }
        }
        return subGraph;
    }

    private static boolean isAcyclic_dfs(Node v, Node p, Set<Node> visited, boolean onlyF) {
        visited.add(v);
        for(Edge e : v.neighbors.values()) {
            if(e.t == p || (!e.t.isF() && onlyF)) continue;
            if(visited.contains(e.t)) return false;
            if(e.c > 1) return false;
            if(!isAcyclic_dfs(e.t, v, visited, onlyF)) return false;
        }
        return true;
    }

    public static boolean isAcyclic(Graph graph) {
        Set<Node> visited = new HashSet<>();
        for(Node v : graph.nodes.values()) {
            if(visited.contains(v)) continue;
            if(!isAcyclic_dfs(v, null, visited, false)) return false;
        }
        return true;
    }

    public static boolean isAcyclicInF(Graph graph) {
        Set<Node> visited = new HashSet<>();
        for(Node v : graph.nodes.values()) {
            if(visited.contains(v) || !v.isF()) continue;
            if(!isAcyclic_dfs(v, null, visited, true)) return false;
        }
        return true;
    }

    public static List<Node> getF(List<Node> C, Graph graph) {
        CompositeCommand command = new CompositeCommand();
        for(Node v : C) {
            Command removeV = new RemoveNodeCommand(v.id, graph);
            command.commands.add(removeV);
        }
        command.execute();
        List<Node> F = new ArrayList<>();
        while(!graph.isolated.isEmpty() || !graph.leaves.isEmpty()) {
            Node v = null;
            if(!graph.leaves.isEmpty()) {
                v = graph.leaves.iterator().next();
            } else {
                v = graph.isolated.iterator().next();
            }
            Command removeV = new RemoveNodeCommand(v.id, graph);
            removeV.execute();
            command.commands.add(removeV);
            F.add(v);
        }
        command.undo();
        return F;
    }

    public static FVC greedyCMaxF(int k, Graph graph) {
        FVC fvc = new FVC(graph);
        CompositeCommand command = new CompositeCommand();
        List<Node> nodes = new ArrayList<>(graph.nodes.values());
        for(int i = 0; i < k; i++) {
            int maxF = -1;
            Node bestC = null;
            List<Node> bestF = null;
            for(Node v : nodes) {
                if(fvc.getC().contains(v) || fvc.getF().contains(v)) continue;
                List<Node> F = getF(Arrays.asList(v), graph);
                if(F.size() > maxF) {
                    maxF = F.size();
                    bestC = v;
                    bestF = F;
                }
                fvc.removeC(v);
            }
            if(bestC == null) break;
            fvc.addC(bestC);
            for(Node u : bestF) {
                fvc.addF(u);
            }
            Command removeC = new RemoveNodeCommand(bestC.id, graph);
            removeC.execute();
            command.commands.add(removeC);
            for(Node v : bestF) {
                Command removeF = new RemoveNodeCommand(v.id, graph);
                removeF.execute();
                command.commands.add(removeF);
            }
        }
        command.undo();
        return fvc;
    }

    public static FVC greedyCMaxDiameter(int k, Graph graph) {
        FVC fvc = new FVC(graph);
        CompositeCommand command = new CompositeCommand();
        List<Node> nodes = new ArrayList<>(graph.nodes.values());
        for(int i = 0; i < k; i++) {
            int maxDiameter = -1;
            Node bestC = null;
            List<Node> bestF = null;
            for(Node v : nodes) {
                if(fvc.getC().contains(v) || fvc.getF().contains(v)) continue;
                List<Node> F = getF(Arrays.asList(v), graph);
                Set<Node> forest = new HashSet<>(F);
                forest.addAll(fvc.getF());
                int diameter = diameterForestSubgraph(forest, graph);
                if(diameter > maxDiameter) {
                    maxDiameter = diameter;
                    bestC = v;
                    bestF = F;
                }
                fvc.removeC(v);
            }
            if(bestC == null) break;
            fvc.addC(bestC);
            for(Node u : bestF) {
                fvc.addF(u);
            }
            Command removeC = new RemoveNodeCommand(bestC.id, graph);
            removeC.execute();
            command.commands.add(removeC);
            for(Node v : bestF) {
                Command removeF = new RemoveNodeCommand(v.id, graph);
                removeF.execute();
                command.commands.add(removeF);
            }
        }
        command.undo();
        return fvc;
    }

    public static int diameterForestSubgraph(Set<Node> forest, Graph graph) {
        Set<Node> visited = new HashSet<>();
        Set<Node> visited2 = new HashSet<>();
        int maxDiameter = 0;
        for(Node v : forest) {
            if(visited.contains(v)) continue;
            visited.add(v);
            List<Node> queue = new ArrayList<>();
            List<Integer> dist = new ArrayList<>();
            queue.add(v);
            dist.add(0);
            for(int i = 0; i < queue.size(); i++) {
                for(Node u : queue.get(i).neighbors.keySet()) {
                    if(!forest.contains(u) || visited.contains(u)) continue;
                    queue.add(u);
                    visited.add(u);
                    dist.add(dist.get(i)+1);
                }
            }
            Node furthest = queue.get(queue.size()-1);
            visited2.add(furthest);
            queue.clear();
            dist.clear();
            queue.add(furthest);
            dist.add(0);
            for(int i = 0; i < queue.size(); i++) {
                for(Node u : queue.get(i).neighbors.keySet()) {
                    if(!forest.contains(u) || visited2.contains(u)) continue;
                    queue.add(u);
                    visited2.add(u);
                    dist.add(dist.get(i)+1);
                }
            }
            maxDiameter = Math.max(maxDiameter, dist.get(dist.size()-1));
        }
        return maxDiameter;
    }

    private static void edgeBCC_dfs(Node v, Node p, Map<Node, Integer> discovery, Map<Node, Integer> low, List<Edge> bridges, int time) {
        discovery.put(v, time);
        low.put(v, time);
        for(Node w : v.neighbors.keySet()) {
            if(w == p && v.neighbors.get(p).c == 1) continue;
            if(!discovery.containsKey(w)) {
                edgeBCC_dfs(w, v, discovery, low, bridges, time+1);
                low.put(v, Math.min(low.get(v), low.get(w)));
                if(low.get(w) > discovery.get(v)) {
                    bridges.add(v.neighbors.get(w));
                }
            } else {
                low.put(v, Math.min(low.get(v), discovery.get(w)));
            }
        }
    }

    public static List<Edge> edgeBCC(Graph graph) {
        List<Edge> bridges = new ArrayList<>();
        Map<Node, Integer> discovery = new HashMap<>();
        Map<Node, Integer> low = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            if(!discovery.containsKey(v)) {
                edgeBCC_dfs(v, null, discovery, low, bridges, 0);
            }
        }
        return bridges;
    }

    public static List<PathAntler> getSingletonPathAntlers(Graph graph, boolean checkF) {
        Map<Node, Set<Node>> pathnodes = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            pathnodes.put(v, new HashSet<>());
        }
        for(Node v : graph.nodes.values()) {
            if(v.nbhSize <= 3) {
                for(Edge e : v.neighbors.values()) {
                    if(v.degree-e.c <= v.nbhSize-1 && !(checkF && e.t.isF())) {
                        pathnodes.get(e.t).add(v);
                    }
                }
            }
        }
        List<PathAntler> pathAntlers = new ArrayList<>();
        for(Node v : graph.nodes.values()) {
            Set<Node> visited = new HashSet<>();
            for(Node w : pathnodes.get(v)) {
                if(visited.contains(w)) continue;
                visited.add(w);
                List<Node> path = new ArrayList<>();
                path.add(w);
                PathAntler pathAntler = new PathAntler(graph);
                for(int i = 0; i < path.size(); i++) {
                    for(Node u : path.get(i).neighbors.keySet()) {
                        if(visited.contains(u) || u == v || !pathnodes.get(v).contains(u)) continue;
                        visited.add(u);
                        path.add(u);
                    }
                }
                pathAntler.addC(v);
                for(Node u : path) {
                    pathAntler.addP(u);
                }
                pathAntler.computeStatistics();
                boolean nonEmptyA = (path.size() >= 4);
                nonEmptyA = (nonEmptyA || (path.size() >= 3 && pathAntler.isCyclic));
                nonEmptyA = (nonEmptyA || (path.size() >= 3 && v.neighbors.get(pathAntler.endpoints[0]).c >= 2));
                nonEmptyA = (nonEmptyA || (path.size() >= 3 && v.neighbors.get(pathAntler.endpoints[1]).c >= 2));
                nonEmptyA = (nonEmptyA || (path.size() >= 2 && v.neighbors.get(pathAntler.endpoints[0]).c >= 2 && v.neighbors.get(pathAntler.endpoints[1]).c >= 2));
                nonEmptyA = (nonEmptyA || graph.selfloop.contains(v));
                if(nonEmptyA) {
                    pathAntler.addA(v);
                }
                pathAntlers.add(pathAntler);
            }
        }
        return pathAntlers;
    }

    public static Map<Pair<Node, Integer>, Integer> hashTable = new HashMap<>();
    public static int maxHash = 1;

    public static int hashNodes(Set<Node> nodes) {
        List<Node> nodeList = new ArrayList<>(nodes);
        Collections.sort(nodeList, (n1, n2) -> n1.id-n2.id);
        int hash = 0;
        for(Node v : nodeList) {
            Pair<Node, Integer> nextPair = new Pair<Node,Integer>(v, hash);
            if(hashTable.get(nextPair) == null) {
                hashTable.put(nextPair, maxHash);
                maxHash++;
            }
            hash = hashTable.get(nextPair);
        }
        return hash;
    }

    public static List<PathAntler> getKPathAntlers(int k, Graph graph, boolean onlyLengthCheck, boolean checkF) {
        if(k == 1) {
            List<PathAntler> singlePathAntlers = getSingletonPathAntlers(graph, checkF);
            List<PathAntler> nonEmptyPathAntlers = new ArrayList<>();
            for(PathAntler pathAntler : singlePathAntlers) {
                if(!pathAntler.getA().isEmpty()) {
                    nonEmptyPathAntlers.add(pathAntler);
                }
            }
            if(nonEmptyPathAntlers.isEmpty()) {
                return singlePathAntlers;
            } else {
                return nonEmptyPathAntlers;
            }
        }
        List<PathAntler> prevPathAntlers = getKPathAntlers(k-1, graph, onlyLengthCheck, checkF);
        if(!prevPathAntlers.isEmpty() && !prevPathAntlers.get(0).getA().isEmpty()) {
            return prevPathAntlers;
        }
        List<PathAntler> nextPathAntlers = new ArrayList<>();
        for(PathAntler pathAntler : prevPathAntlers) {
            for(int i = 0; i < 2; i++) {
                Node nextNode = pathAntler.nextnodes[i];
                if(nextNode == null || pathAntler.extended[i] || graph.selfloop.contains(nextNode)) continue;
                Node nbInF = null;
                boolean possible = true;
                int nbh = 0;
                for(Node nb : nextNode.neighbors.keySet()) {
                    if(pathAntler.getC().contains(nb) || pathAntler.getP().contains(nb)) continue;
                    nbh++;
                    if(nb.isF() && nbInF == null && checkF) {
                        nbInF = nb;
                        if(nextNode.neighbors.get(nb).c >= 2) {
                            possible = false;
                            break;
                        }
                    } else if(nb.isF() && checkF) {
                        possible = false;
                        break;
                    }
                }
                if(!possible) {
                    pathAntler.extended[i] = true;
                    continue;
                }
                if(nbh + pathAntler.getC().size() == k+1) {
                    if(nbInF != null) {
                        pathAntler.extended[i] = true;
                        PathAntler newPathAntler = new PathAntler(graph);
                        for(Node u : pathAntler.getC()) {
                            newPathAntler.addC(u);
                        }
                        for(Node w : nextNode.neighbors.keySet()) {
                            if(pathAntler.getP().contains(w) || pathAntler.getC().contains(w) || nbInF == w) continue;
                            newPathAntler.addC(w);
                        }
                        for(Node u : pathAntler.getP()) {
                            newPathAntler.addP(u);
                        }
                        newPathAntler.endpoints[0] = pathAntler.endpoints[0];
                        newPathAntler.endpoints[1] = pathAntler.endpoints[1];
                        newPathAntler.nextnodes[0] = newPathAntler.getC().contains(pathAntler.nextnodes[0]) ? null : pathAntler.nextnodes[0];
                        newPathAntler.nextnodes[1] = newPathAntler.getC().contains(pathAntler.nextnodes[1]) ? null : pathAntler.nextnodes[1];
                        newPathAntler.extendP(true);
                        nextPathAntlers.add(newPathAntler);
                        continue;
                    }
                    for(Node v : nextNode.neighbors.keySet()) {
                        if(pathAntler.getP().contains(v) || pathAntler.getC().contains(v) || nextNode.neighbors.get(v).c >= 2) continue;
                        pathAntler.extended[i] = true;
                        PathAntler newPathAntler = new PathAntler(graph);
                        for(Node u : pathAntler.getC()) {
                            newPathAntler.addC(u);
                        }
                        for(Node w : nextNode.neighbors.keySet()) {
                            if(pathAntler.getP().contains(w) || pathAntler.getC().contains(w) || v == w) continue;
                            newPathAntler.addC(w);
                        }
                        for(Node u : pathAntler.getP()) {
                            newPathAntler.addP(u);
                        }
                        newPathAntler.endpoints[0] = pathAntler.endpoints[0];
                        newPathAntler.endpoints[1] = pathAntler.endpoints[1];
                        newPathAntler.nextnodes[0] = newPathAntler.getC().contains(pathAntler.nextnodes[0]) ? null : pathAntler.nextnodes[0];
                        newPathAntler.nextnodes[1] = newPathAntler.getC().contains(pathAntler.nextnodes[1]) ? null : pathAntler.nextnodes[1];
                        newPathAntler.extendP(true);
                        nextPathAntlers.add(newPathAntler);
                    }
                } else if(nbh + pathAntler.getC().size() <= k) {
                    if(nbInF != null) {
                        pathAntler.extended[i] = true;
                        PathAntler newPathAntler = new PathAntler(graph);
                        for(Node u : pathAntler.getC()) {
                            newPathAntler.addC(u);
                        }
                        for(Node w : nextNode.neighbors.keySet()) {
                            if(pathAntler.getP().contains(w) || pathAntler.getC().contains(w) || nbInF == w) continue;
                            newPathAntler.addC(w);
                        }
                        for(Node u : pathAntler.getP()) {
                            newPathAntler.addP(u);
                        }
                        newPathAntler.endpoints[0] = pathAntler.endpoints[0];
                        newPathAntler.endpoints[1] = pathAntler.endpoints[1];
                        newPathAntler.nextnodes[0] = newPathAntler.getC().contains(pathAntler.nextnodes[0]) ? null : pathAntler.nextnodes[0];
                        newPathAntler.nextnodes[1] = newPathAntler.getC().contains(pathAntler.nextnodes[1]) ? null : pathAntler.nextnodes[1];
                        newPathAntler.extendP(true);
                        nextPathAntlers.add(newPathAntler);
                        continue;
                    }
                    pathAntler.extended[i] = true;
                    PathAntler newPathAntler = new PathAntler(graph);
                    for(Node u : pathAntler.getC()) {
                        newPathAntler.addC(u);
                    }
                    for(Node w : nextNode.neighbors.keySet()) {
                        if(pathAntler.getP().contains(w) || pathAntler.getC().contains(w)) continue;
                        newPathAntler.addC(w);
                    }
                    for(Node u : pathAntler.getP()) {
                        newPathAntler.addP(u);
                    }
                    newPathAntler.endpoints[0] = pathAntler.endpoints[0];
                    newPathAntler.endpoints[1] = pathAntler.endpoints[1];
                    newPathAntler.nextnodes[0] = newPathAntler.getC().contains(pathAntler.nextnodes[0]) ? null : pathAntler.nextnodes[0];
                    newPathAntler.nextnodes[1] = newPathAntler.getC().contains(pathAntler.nextnodes[1]) ? null : pathAntler.nextnodes[1];
                    newPathAntler.extendP(true);
                    nextPathAntlers.add(newPathAntler);
                }
            }
            if((!pathAntler.extended[0] && pathAntler.nextnodes[0] != null) || (!pathAntler.extended[1] && pathAntler.nextnodes[1] != null)) {
                nextPathAntlers.add(pathAntler);
            }
        }
        Map<Integer, Set<Node>> hashToC = new HashMap<>();
        Map<Integer, Set<Node>> hashToP = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            if(v.nbhSize <= k+2 && v.nbhSize >= k && !graph.selfloop.contains(v)) {
                List<Node> nbhNodes = new ArrayList<>(v.neighbors.keySet());
                boolean iLoop = false;
                boolean jLoop = false;
                if(nbhNodes.size() >= k+1) {
                    iLoop = true;
                }
                if(nbhNodes.size() >= k+2) {
                    jLoop = true;
                }
                for(int i = (iLoop ? 0 : -2); i < (iLoop ? nbhNodes.size() : -1); i++) {
                    for(int j = (jLoop ? i+1 : -2); j < (jLoop ? nbhNodes.size() : -1); j++) {
                        if(iLoop && (nbhNodes.get(i).neighbors.get(v).c >= 2 || (nbhNodes.get(i).isF() && checkF))) continue;
                        if(jLoop && (nbhNodes.get(j).neighbors.get(v).c >= 2 || (nbhNodes.get(j).isF() && checkF))) continue;
                        Set<Node> nodeSet = new HashSet<>();
                        for(int l = 0; l < nbhNodes.size(); l++) {
                            if(l == i || l == j) continue;
                            nodeSet.add(nbhNodes.get(l));
                        }
                        int hash = hashNodes(nodeSet);
                        if(hashToC.get(hash) == null) hashToC.put(hash, nodeSet);
                        if(hashToP.get(hash) == null) hashToP.put(hash, new HashSet<>());
                        hashToP.get(hash).add(v);
                    }
                }
            }
        }
        for(int hash : hashToP.keySet()) {
            Set<Node> visited = new HashSet<>();
            for(Node v : hashToP.get(hash)) {
                if(visited.contains(v)) continue;
                visited.add(v);
                List<Node> queue = new ArrayList<>();
                queue.add(v);
                for(int i = 0; i < queue.size(); i++) {
                    for(Node w : queue.get(i).neighbors.keySet()) {
                        if(visited.contains(w) || !hashToP.get(hash).contains(w)) continue;
                        visited.add(w);
                        queue.add(w);
                    }
                }
                PathAntler pathAntler = new PathAntler(graph);
                for(Node u : hashToC.get(hash)) {
                    pathAntler.addC(u);
                }
                for(Node u : queue) {
                    pathAntler.addP(u);
                }
                pathAntler.computeStatistics();
                int pSize = pathAntler.getP().size();
                pathAntler.extendP(true);
                if(pSize == pathAntler.getP().size()) {
                    nextPathAntlers.add(pathAntler);
                }
            }
        }
        Set<PathAntler> uniques = new HashSet<>(nextPathAntlers);
        List<PathAntler> nonEmptyPathAntlers = new ArrayList<>();
        for(PathAntler pathAntler : uniques) {
            if(onlyLengthCheck) {
                for(Node c : pathAntler.getC()) {
                    if(hasFlower(pathAntler.getP(), c) >= pathAntler.getC().size()+1) {
                        pathAntler.addA(c);
                    }
                }
            } else {
                pathAntler.computeMaxA();
            }
            if(!pathAntler.getA().isEmpty()) {
                nonEmptyPathAntlers.add(pathAntler);
            }
        }
        if(nonEmptyPathAntlers.isEmpty()) {
            return new ArrayList<>(uniques);
        } else {
            return nonEmptyPathAntlers;
        }
    }

    public static List<FVC> find2Antlers(Graph graph, boolean checkF) {
        // TODO finish this with actual path antler 1 handling instead of 2
        List<FVC> fvcList = new ArrayList<>();
        List<PathAntler> singletonPathAntlers = getSingletonPathAntlers(graph, checkF);
        for(PathAntler pathAntler : singletonPathAntlers) {
            if(pathAntler.isCyclic) {
                fvcList.add(new FVC(graph, new HashSet<>(Arrays.asList(new Node[]{pathAntler.getC().iterator().next(), pathAntler.getP().iterator().next()}))));

            }
        }
        return null;
    }

    private static void extendC(Set<Node> c, int i, int k, List<Node> nodes, Set<Set<Node>> extension, boolean checkF) {
        if(c.size() == k) {
            extension.add(new HashSet<>(c));
            return;
        }
        for(int j = i; j < nodes.size(); j++) {
            if((!checkF || !nodes.get(j).isF()) && !c.contains(nodes.get(j))) {
                c.add(nodes.get(j));
                extendC(c, j+1, k, nodes, extension, checkF);
                c.remove(nodes.get(j));
            }
        }
    }

    public static List<FVC> findKAntlers(int k, Graph graph, boolean onlyFlower, boolean checkF) {
        // Assumes that no path-antlers of size k exist in the graph for efficiency guarentee
        Set<Set<Node>> Cs = new HashSet<>();
        for(Node v : graph.nodes.values()) {
            if(v.nbhSize <= k+1) {
                Node nbInF = null;
                if(checkF) {
                    boolean possible = true;
                    for(Edge e : v.neighbors.values()) {
                        if(e.t.isF() && nbInF == null) {
                            nbInF = e.t;
                            if(e.c >= 2) {
                                possible = false;
                                break;
                            }
                        } else if(e.t.isF()) {
                            possible = false;
                            break;
                        }
                    }
                    if(!possible) {
                        continue;
                    }
                }
                if(nbInF != null) {
                    Set<Node> c = new HashSet<>(v.neighbors.keySet());
                    c.remove(nbInF);
                    Cs.add(c);
                    continue;
                }
                for(Edge e : v.neighbors.values()) {
                    if(e.c >= 2) continue;
                    Set<Node> c = new HashSet<>(v.neighbors.keySet());
                    c.remove(e.t);
                    Cs.add(c);
                }
                if(v.nbhSize <= k) {
                    Cs.add(new HashSet<>(v.neighbors.keySet()));
                }
            }
        }
        Map<Integer, Set<Node>> ExtendedCs = new HashMap<>();
        List<Node> nodes = new ArrayList<>(graph.nodes.values());
        for(Set<Node> c : Cs) {
            Set<Set<Node>> extension = new HashSet<>();
            extendC(c, 0, k, nodes, extension, checkF);
            for(Set<Node> extendedC : extension) {
                ExtendedCs.put(hashNodes(extendedC), extendedC);
            }
        }
        List<FVC> nonEmptyFVC = new ArrayList<>();
        for(Set<Node> C : ExtendedCs.values()) {
            FVC fvc = new FVC(graph, C);
            fvc.computeMaxA(onlyFlower);
            if(!fvc.getA().isEmpty()) {
                nonEmptyFVC.add(fvc);
            }
        }
        return nonEmptyFVC;
    }

    private static Pair<Integer, Integer> hasFlower_dfs(Node r, Node v, Set<Node> F, Set<Node> visited) {
        visited.add(r);
        int subtreeCycles = 0;
        int extraCycles = 0;
        for(Node u : r.neighbors.keySet()) {
            if(!F.contains(u) || visited.contains(u)) continue;
            Pair<Integer, Integer> pair = hasFlower_dfs(u, v, F, visited);
            subtreeCycles += pair.a;
            extraCycles += pair.b;
        }
        if(extraCycles >= 2 || (v.neighbors.get(r) != null && (extraCycles + v.neighbors.get(r).c >= 2))) {
            return new Pair<Integer,Integer>(subtreeCycles+1, 0);
        } else if(extraCycles >= 1 || v.neighbors.get(r) != null) {
            return new Pair<Integer,Integer>(subtreeCycles, 1);
        } else {
            return new Pair<Integer,Integer>(subtreeCycles, 0);
        }
    }

    public static int hasFlower(Set<Node> F, Node v) {
        Set<Node> visited = new HashSet<>();
        int treeCycles = 0;
        for(Node r : F) {
            if(visited.contains(r)) continue;
            treeCycles += hasFlower_dfs(r, v, F, visited).a;
        }
        return treeCycles;
    }

    public static boolean isFVS(List<Node> nodes, Graph graph) {
        CompositeCommand nodeControl = new CompositeCommand();
        for(Node v : nodes) {
            Command removeV = new RemoveNodeCommand(v.id, graph);
            nodeControl.commands.add(removeV);
        }
        nodeControl.execute();
        boolean isFVS = isAcyclic(graph);
        nodeControl.undo();
        return isFVS;
    }

    private static List<Node> naiveFVS_dfs(int k, int i, List<Node> nodes, List<Node> build, Graph graph) {
        if(build.size() <= k) {
            CompositeCommand command = new CompositeCommand();
            for(Node v : build) {
                RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
                command.commands.add(removeV);
            }
            command.execute();
            boolean acyclic = GraphAlgorithm.isAcyclic(graph);
            command.undo();
            if(acyclic) {
                return new ArrayList<>(build);
            }
        } else {
            return null;
        }
        if(i >= nodes.size()) return null;
        List<Node> best1 = naiveFVS_dfs(k, i+1, nodes, build, graph);
        build.add(nodes.get(i));
        List<Node> best2 = naiveFVS_dfs(k, i+1, nodes, build, graph);
        build.remove(build.size()-1);
        if(best1 == null && best2 == null) {
            return null;
        } else if(best2 == null) {
            return best1;
        } else if(best1 == null) {
            return best2;
        } else {
            return best1.size() <= best2.size() ? best1 : best2;
        }
    }

    public static List<Node> naiveFVS(int k, Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.nodes.values());
        return naiveFVS_dfs(k, 0, nodes, new ArrayList<>(), graph);
    }

    public static List<Node> naiveFVS(Graph graph) {
        return naiveFVS(graph.nodecount, graph);
    }

    public static List<Node> naiveDisjointFVS(Node v, int k, Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.nodes.values());
        nodes.remove(v);
        return naiveFVS_dfs(k, 0, nodes, new ArrayList<>(), graph);
    }

    public static List<Node> naiveDisjointFVS(Node v, Graph graph) {
        return naiveDisjointFVS(v, graph.nodecount, graph);
    }

    public static List<Node> smartFVS(int k, Graph graph) {
        Map<Node, Integer> map = new HashMap<>();
        Map<Integer, Node> mapBack = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            map.put(v, map.size());
            mapBack.put(map.get(v), v);
        }
        int[][] adj = new int[map.size()][0];
        for(Node v : graph.nodes.values()) {
            adj[map.get(v)] = new int[v.degree];
            int index = 0;
            for(Edge e : v.neighbors.values()) {
                for(int i = 0; i < e.c; i++) {
                    adj[map.get(v)][index] = map.get(e.t);
                    index++;
                }
            }
            Arrays.sort(adj[map.get(v)]);
        }
        fvs_wata_orz.Graph fvsGraph = new fvs_wata_orz.Graph(adj);
        fvs_wata_orz.Solver solver = new fvs_wata_orz.FPTBranchingSolver();
        fvs_wata_orz.tc.wata.debug.Debug.silent = true;
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        solver.ub = k;
        solver.solve(fvsGraph);
        List<Node> fvs = new ArrayList<>();
        if(solver.res == null) {
            return null;
        }
        for(int i : solver.res) {
            fvs.add(mapBack.get(i));
        }
        return fvs;
    }

    public static List<Node> smartFVS(Graph graph) {
        return smartFVS(Integer.MAX_VALUE, graph);
    }

    public static List<Node> smartDisjointFVS(Node s, int k, Graph graph) {
        if(s.neighbors.get(s) != null) return null;
        Map<Node, Integer> map = new HashMap<>();
        Map<Integer, Node> mapBack = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            map.put(v, map.size());
            mapBack.put(map.get(v), v);
        }
        int[][] adj = new int[map.size()][0];
        for(Node v : graph.nodes.values()) {
            adj[map.get(v)] = new int[v.degree];
            int index = 0;
            for(Edge e : v.neighbors.values()) {
                for(int i = 0; i < e.c; i++) {
                    adj[map.get(v)][index] = map.get(e.t);
                    index++;
                }
            }
            Arrays.sort(adj[map.get(v)]);
        }
        fvs_wata_orz.Graph fvsGraph = new fvs_wata_orz.Graph(adj);
        fvs_wata_orz.FPTBranchingSolver solver = new fvs_wata_orz.FPTBranchingSolver();
        fvs_wata_orz.tc.wata.debug.Debug.silent = true;
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        fvsGraph.setF(map.get(s));
        solver.ub = k+1;
        solver.solve(fvsGraph, map.get(s));
        List<Node> fvs = new ArrayList<>();
        if(solver.res == null) {
            return null;
        }
        for(int i : solver.res) {
            fvs.add(mapBack.get(i));
        }
        return fvs;
    }
}
