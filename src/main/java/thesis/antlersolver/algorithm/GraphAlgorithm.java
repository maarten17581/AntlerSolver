package thesis.antlersolver.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.PathAntler;

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

    public static boolean isAcyclic(Graph graph) {
        List<List<Node>> cc = connectedComponents(graph);
        for(int i = 0; i < cc.size(); i++) {
            int edgeCounter = 0;
            for(Node v : cc.get(i)) {
                edgeCounter += v.degree;
            }
            if(edgeCounter > 2*cc.get(i).size()-2) {
                return false;
            }
        }
        return true;
    }

    public static List<Edge> EdgeBCC(Graph graph) {
        List<Edge> bridges = new ArrayList<>();
        Map<Node, Integer> discovery = new HashMap<>();
        Map<Node, Integer> low = new HashMap<>();
        class DFS {
            void dfs(Node v, Node p, Map<Node, Integer> discovery, Map<Node, Integer> low, List<Edge> bridges, int time) {
                discovery.put(v, time);
                low.put(v, time);
                for(Node w : v.neighbors.keySet()) {
                    if(w == p && v.neighbors.get(p).c == 1) continue;
                    if(!discovery.containsKey(w)) {
                        dfs(w, v, discovery, low, bridges, time+1);
                        low.put(v, Math.min(low.get(v), low.get(w)));
                        if(low.get(w) > discovery.get(v)) {
                            bridges.add(v.neighbors.get(w));
                        }
                    } else {
                        low.put(v, Math.min(low.get(v), discovery.get(w)));
                    }
                }
            }
        }
        DFS dfs = new DFS();
        for(Node v : graph.nodes.values()) {
            if(!discovery.containsKey(v)) {
                dfs.dfs(v, null, discovery, low, bridges, 0);
            }
        }
        return bridges;
    }

    public static List<PathAntler> getSingletonPathAntlers(Graph graph) {
        Map<Node, Set<Node>> pathnodes = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            pathnodes.put(v, new HashSet<>());
        }
        for(Node v : graph.nodes.values()) {
            if(v.nbhSize <= 3) {
                for(Edge e : v.neighbors.values()) {
                    if(v.degree-e.c <= v.nbhSize-1) {
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
                for(int i = 0; i < path.size(); i++) {
                    for(Node u : path.get(i).neighbors.keySet()) {
                        if(visited.contains(u) || u == v || !pathnodes.get(v).contains(u)) continue;
                        visited.add(u);
                        path.add(u);
                    }
                }
                PathAntler pathAntler = new PathAntler(graph);
                pathAntler.C.add(v);
                pathAntler.P = path;
                pathAntlers.add(pathAntler);
            }
        }
        return pathAntlers;
    }
}
