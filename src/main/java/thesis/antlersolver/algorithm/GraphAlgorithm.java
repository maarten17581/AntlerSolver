package thesis.antlersolver.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;

public class GraphAlgorithm {

    public static List<List<Node>> connectedComponents(Graph graph) {
        class DFS {
            public void dfs(Node v, Set<Node> component) {
                for(Node w : v.neighbors.keySet()) {
                    if(component.contains(w)) continue;
                    component.add(w);
                    dfs(w, component);
                }
            }
        }
        Set<Node> found = new HashSet<>();
        List<List<Node>> components = new ArrayList<>();
        DFS dfs = new DFS();
        for(Node v : graph.nodes.values()) {
            if(found.contains(v)) continue;
            Set<Node> component = new HashSet<>();
            dfs.dfs(v, component);
            components.add(new ArrayList<>(component));
            found.addAll(component);
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
}
