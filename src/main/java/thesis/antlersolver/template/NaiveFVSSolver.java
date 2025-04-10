package thesis.antlersolver.template;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.strategy.branching.Brancher;
import thesis.antlersolver.strategy.branching.NaiveBrancher;
import thesis.antlersolver.strategy.splitting.ComponentSplitter;
import thesis.antlersolver.strategy.splitting.Splitter;

public class NaiveFVSSolver extends AbstractFVSSolver {

    protected Pair<Command, List<Node>> startKernel(Graph graph) {
        return new Pair<Command, List<Node>>(null, new ArrayList<>());
    }

    protected Brancher branchStep(int k, Graph graph) {
        return new NaiveBrancher(graph);
    }

    protected Pair<Command, List<Node>> kernelStep(int k, Graph graph) {
        return new Pair<Command, List<Node>>(null, new ArrayList<>());
    }

    protected Splitter splitStep(Graph graph) {
        return new ComponentSplitter(graph);
    }

    protected boolean prune(int k, Graph graph) {
        return k < 0 || !GraphAlgorithm.isAcyclicInF(graph);
    }
    
}