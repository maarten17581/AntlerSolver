package thesis.antlersolver.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.command.SetNodeFCommand;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.statistics.Statistics;
import thesis.antlersolver.strategy.branching.Brancher;
import thesis.antlersolver.strategy.branching.MaxDegreeBrancher;
import thesis.antlersolver.strategy.branching.MaxDegreeConnectedBrancher;
import thesis.antlersolver.strategy.kernalization.CompositeKernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.ContractFStrategy;
import thesis.antlersolver.strategy.kernalization.CycleWithFStrategy;
import thesis.antlersolver.strategy.kernalization.Degree2Strategy;
import thesis.antlersolver.strategy.kernalization.EdgeBCCStrategy;
import thesis.antlersolver.strategy.kernalization.IsolatedStrategy;
import thesis.antlersolver.strategy.kernalization.IwataKernalizationStepStrategy;
import thesis.antlersolver.strategy.kernalization.IwataKernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.KAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.KPathAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.KernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.LeafStrategy;
import thesis.antlersolver.strategy.kernalization.MultiEdgeStrategy;
import thesis.antlersolver.strategy.kernalization.SelfloopStrategy;
import thesis.antlersolver.strategy.kernalization.SingleAntlerStrategy;
import thesis.antlersolver.strategy.splitting.ComponentSplitter;
import thesis.antlersolver.strategy.splitting.Splitter;

public class MaxDegreeFVSSolver extends AbstractFVSSolver {

    KernalizationStrategy startStrategy = new IwataKernalizationStrategy();
    KernalizationStrategy beginStepStrategy = new CompositeKernalizationStrategy(new KernalizationStrategy[]{
        new ContractFStrategy(),
        new CycleWithFStrategy(),
        new IsolatedStrategy(),
        new LeafStrategy(),
        new Degree2Strategy(),
        new MultiEdgeStrategy(),
        new SelfloopStrategy(),
        new SingleAntlerStrategy()
    });
    KernalizationStrategy componentStrategy = new EdgeBCCStrategy();
    KernalizationStrategy[] afterComponentStrategies = new KernalizationStrategy[]{
        new KPathAntlerStrategy(2, false, true),
        new KAntlerStrategy(2, false, true),
        new KPathAntlerStrategy(3, false, true),
        new KAntlerStrategy(3, false, true),
        new KPathAntlerStrategy(4, false, true),
        new KAntlerStrategy(4, false, true)
    };

    public Pair<Command, List<Node>> startKernel(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Pair<Command, List<Node>> startPair = startStrategy.apply(graph);
        command.commands.add(startPair.a);
        solutionSet.addAll(startPair.b);
        if(GraphAlgorithm.connectedComponents(graph).size() > 1) {
            return new Pair<Command,List<Node>>(command, solutionSet);
        }
        Pair<Command, List<Node>> stepPair = kernelStep(graph.nodecount, graph);
        command.commands.add(stepPair.a);
        solutionSet.addAll(stepPair.b);
        return new Pair<Command,List<Node>>(command, solutionSet);
    }

    public Brancher branchStep(int k, Graph graph) {
        return new MaxDegreeConnectedBrancher(graph);
    }

    public Pair<Command, List<Node>> kernelStep(int k, Graph graph) {
        Pair<Command, List<Node>> beginPair = beginStepStrategy.exhaustiveApply(graph);
        Pair<Command, List<Node>> componentPair = componentStrategy.apply(graph);
        List<Node> subsolution = new ArrayList<>();
        CompositeCommand command = new CompositeCommand();
        if(beginPair != null) {
            command.commands.add(beginPair.a);
            subsolution.addAll(beginPair.b);
        }
        if(componentPair != null) {
            command.commands.add(componentPair.a);
            subsolution.addAll(componentPair.b);
            return new Pair<Command, List<Node>>(command, subsolution);
        }
        if(graph.nodecount == 0) {
            return new Pair<Command, List<Node>>(command, subsolution);
        }
        IwataKernalizationStepStrategy kernalizationStepStrategy = new IwataKernalizationStepStrategy(k);
        Pair<Command, List<Node>> nextKernel = kernalizationStepStrategy.apply(graph);
        command.commands.add(nextKernel.a);
        subsolution.addAll(nextKernel.b);
        for(int i = 0; i < afterComponentStrategies.length; i++) {
            if((graph.nodecount > 100 && i > 1) || (graph.nodecount > 50 && i > 3)) break;
            KernalizationStrategy strategy = afterComponentStrategies[i];
            Pair<Command, List<Node>> complicatedPair = strategy.apply(graph);
            if(complicatedPair != null) {
                command.commands.add(complicatedPair.a);
                subsolution.addAll(complicatedPair.b);
                i = -1;
            } else {
                continue;
            }
            Pair<Command, List<Node>> assumptionPair = beginStepStrategy.exhaustiveApply(graph);
            if(assumptionPair != null) {
                command.commands.add(assumptionPair.a);
                subsolution.addAll(assumptionPair.b);
            }
            Pair<Command, List<Node>> newComponentPair = componentStrategy.apply(graph);
            if(newComponentPair != null) {
                command.commands.add(newComponentPair.a);
                subsolution.addAll(newComponentPair.b);
                break;
            }
        }
        return new Pair<Command, List<Node>>(command, subsolution);
    }

    public Splitter splitStep(Graph graph) {
        return new ComponentSplitter(graph);
    }

    public boolean prune(int k, Graph graph) {
        if(k < 0 || !GraphAlgorithm.isAcyclicInF(graph)) return true;
        int maxDegreeNotF = 0;
        int sumDegreeF = 0;
        for(Node v : graph.nodes.values()) {
            if(v.isF()) {
                sumDegreeF += v.degree-2;
            } else {
                maxDegreeNotF = Math.max(maxDegreeNotF, v.degree);
            }
        }
        if(k*maxDegreeNotF < sumDegreeF) return true;
        Node inF = null;
        for(Node v : graph.nodes.values()) {
            if(v.isF() && inF == null) {
                inF = v;
            } else if(v.isF()) {
                return false;
            }
        }
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
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        return k < fvs_wata_orz.LowerBound.lowerBound(fvsGraph);
    }

    public boolean canSolveLeaf(int k, Graph graph) {
        return graph.nodecount <= 20;
    }

    public List<Node> solveLeaf(int k, Graph graph) {
        long time = -System.currentTimeMillis();
        List<Node> fvs = GraphAlgorithm.smartFVS(k+1, graph);
        time += System.currentTimeMillis();
        Statistics.getStat().count("BranchTreeLeafSolve");
        Statistics.getStat().count("BranchTreeLeafSolveTime", time);
        return fvs;
    }
}