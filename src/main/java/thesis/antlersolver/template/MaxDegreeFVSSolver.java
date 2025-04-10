package thesis.antlersolver.template;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.strategy.branching.Brancher;
import thesis.antlersolver.strategy.branching.MaxDegreeBrancher;
import thesis.antlersolver.strategy.kernalization.CompositeKernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.ContractFStrategy;
import thesis.antlersolver.strategy.kernalization.CycleWithFStrategy;
import thesis.antlersolver.strategy.kernalization.Degree2Strategy;
import thesis.antlersolver.strategy.kernalization.EdgeBCCStrategy;
import thesis.antlersolver.strategy.kernalization.IsolatedStrategy;
import thesis.antlersolver.strategy.kernalization.IwataKernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.KAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.KPathAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.KernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.LeafStrategy;
import thesis.antlersolver.strategy.kernalization.MultiEdgeStrategy;
import thesis.antlersolver.strategy.kernalization.SelfloopStrategy;
import thesis.antlersolver.strategy.kernalization.SingleAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.SingletonPathAntlerStrategy;
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
        new SingletonPathAntlerStrategy(),
        new KPathAntlerStrategy(2, false, true),
        new KAntlerStrategy(2, true)
    };

    protected Pair<Command, List<Node>> startKernel(Graph graph) {
        return startStrategy.apply(graph);
    }

    protected Brancher branchStep(int k, Graph graph) {
        return new MaxDegreeBrancher(graph);
    }

    protected Pair<Command, List<Node>> kernelStep(int k, Graph graph) {
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
        boolean exhaustive = false;
        while(!exhaustive) {
            for(KernalizationStrategy strategy : afterComponentStrategies) {
                Pair<Command, List<Node>> complicatedPair = strategy.apply(graph);
                if(complicatedPair != null) {
                    command.commands.add(complicatedPair.a);
                    subsolution.addAll(complicatedPair.b);
                } else {
                    exhaustive = true;
                    break;
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
                    exhaustive = true;
                    break;
                }
            }
        }
        return new Pair<Command, List<Node>>(command, subsolution);
    }

    protected Splitter splitStep(Graph graph) {
        return new ComponentSplitter(graph);
    }

    protected boolean prune(int k, Graph graph) {
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
    
}