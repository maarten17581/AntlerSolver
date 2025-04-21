package thesis.antlersolver.template;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
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

public class OnlyStartFVSSolver extends AbstractFVSSolver {

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
        List<Node> fvs = GraphAlgorithm.smartFVS(graph);
        CompositeCommand command2 = new CompositeCommand();
        for(Node v : fvs) {
            Command removeV = new RemoveNodeCommand(v.id, graph);
            command2.commands.add(removeV);
        }
        command2.execute();
        if(true) return new Pair<Command,List<Node>>(command2, fvs);
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Pair<Command, List<Node>> startPair = startStrategy.apply(graph);
        command.commands.add(startPair.a);
        solutionSet.addAll(startPair.b);
        if(GraphAlgorithm.connectedComponents(graph).size() > 1) {
            return new Pair<Command,List<Node>>(command, solutionSet);
        }
        Pair<Command, List<Node>> beginPair = beginStepStrategy.exhaustiveApply(graph);
        Pair<Command, List<Node>> componentPair = componentStrategy.apply(graph);
        if(beginPair != null) {
            command.commands.add(beginPair.a);
            solutionSet.addAll(beginPair.b);
        }
        if(componentPair != null) {
            command.commands.add(componentPair.a);
            solutionSet.addAll(componentPair.b);
            return new Pair<Command, List<Node>>(command, solutionSet);
        }
        if(graph.nodecount == 0) {
            return new Pair<Command, List<Node>>(command, solutionSet);
        }
        for(int i = 0; i < afterComponentStrategies.length; i++) {
            if((graph.nodecount > 100 && i > 1) || (graph.nodecount > 50 && i > 3)) break;
            KernalizationStrategy strategy = afterComponentStrategies[i];
            Pair<Command, List<Node>> complicatedPair = strategy.apply(graph);
            if(complicatedPair != null) {
                command.commands.add(complicatedPair.a);
                solutionSet.addAll(complicatedPair.b);
                i = -1;
            } else {
                continue;
            }
            Pair<Command, List<Node>> assumptionPair = beginStepStrategy.exhaustiveApply(graph);
            if(assumptionPair != null) {
                command.commands.add(assumptionPair.a);
                solutionSet.addAll(assumptionPair.b);
            }
            Pair<Command, List<Node>> newComponentPair = componentStrategy.apply(graph);
            if(newComponentPair != null) {
                command.commands.add(newComponentPair.a);
                solutionSet.addAll(newComponentPair.b);
                break;
            }
        }
        return new Pair<Command,List<Node>>(command, solutionSet);
    }

    public Brancher branchStep(int k, Graph graph) {
        return new MaxDegreeConnectedBrancher(graph);
    }

    public Pair<Command, List<Node>> kernelStep(int k, Graph graph) {
        return new Pair<Command, List<Node>>(new CompositeCommand(), new ArrayList<>());
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
        return k*maxDegreeNotF < sumDegreeF;
    }

    public boolean canSolveLeaf(int k, Graph graph) {
        return true;
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