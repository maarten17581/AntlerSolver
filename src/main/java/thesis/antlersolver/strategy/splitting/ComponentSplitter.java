package thesis.antlersolver.strategy.splitting;

import java.util.Collections;
import java.util.List;

import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.model.Graph;

public class ComponentSplitter implements Splitter {

    List<Graph> graphs;
    int index;

    public ComponentSplitter(Graph graph) {
        graphs = GraphAlgorithm.connectedComponentsGraph(graph);
        Collections.sort(graphs, (Graph g1, Graph g2) -> g1.nodecount-g2.nodecount);
        index = 0;
    }

    public Graph next() {
        if(index < graphs.size()) {
            return graphs.get(index++);
        } else {
            return null;
        }
    }

    public int graphNum() {
        return graphs.size();
    }
}
