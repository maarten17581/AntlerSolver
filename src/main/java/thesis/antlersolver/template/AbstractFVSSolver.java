package thesis.antlersolver.template;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.strategy.branching.Brancher;
import thesis.antlersolver.strategy.splitting.Splitter;

public abstract class AbstractFVSSolver {

    public List<Node> solve(Graph graph, long start, long time) {
        Pair<Command, List<Node>> startPair = startKernel(graph);
        if(GraphAlgorithm.isAcyclic(graph)) {
            if(startPair.a != null) {
                startPair.a.undo();
            }
            return startPair.b;
        }
        for(int i = 0; i < graph.nodecount; i++) {
            if(System.currentTimeMillis()-start >= time) return null;
            List<Node> solution = solve(i, graph, start, time);
            if(solution != null) {
                if(startPair.a != null) {
                    startPair.a.undo();
                }
                solution.addAll(startPair.b);
                return solution;
            }
        }
        return null;
    }

    public List<Node> solve(int k, Graph graph, long start, long time) {
        if(prune(k, graph)) return null;
        if(GraphAlgorithm.isAcyclic(graph)) return new ArrayList<>();
        if(System.currentTimeMillis()-start >= time) return null;
        if(canSolveLeaf(k, graph)) return solveLeaf(k, graph);
        Splitter splitter = splitStep(graph);
        if(splitter.graphNum() > 2) {
            List<Node> subcurrent = new ArrayList<>();
            Graph subgraph = splitter.next();
            while(subgraph != null) {
                List<Node> subsolve = solve(k-subcurrent.size(), subgraph, start, time);
                if(subsolve == null) return null;
                if(System.currentTimeMillis()-start >= time) return null;
                subcurrent.addAll(subsolve);
                subgraph = splitter.next();
            }
            return subcurrent;
        } else {
            Brancher brancher = branchStep(k, graph);
            while(brancher.hasNext()) {
                List<Node> branch = brancher.next();
                Pair<Command, List<Node>> kernelPair = kernelStep(k, graph);
                List<Node> solution = solve(k-branch.size()-kernelPair.b.size(), graph, start, time);
                if(kernelPair.a != null) kernelPair.a.undo();
                if(solution != null) {
                    solution.addAll(branch);
                    solution.addAll(kernelPair.b);
                    brancher.reset();
                    return solution;
                }
                if(System.currentTimeMillis()-start >= time) {
                    brancher.reset();
                    return null;
                }
            }
            brancher.reset();
            return null;
        }
    }

    public abstract Pair<Command, List<Node>> startKernel(Graph graph);
    public abstract Brancher branchStep(int k, Graph graph);
    public abstract Pair<Command, List<Node>> kernelStep(int k, Graph graph);
    public abstract Splitter splitStep(Graph graph);
    public abstract boolean prune(int k, Graph graph);
    public abstract boolean canSolveLeaf(int k, Graph graph);
    public abstract List<Node> solveLeaf(int k, Graph graph);
}