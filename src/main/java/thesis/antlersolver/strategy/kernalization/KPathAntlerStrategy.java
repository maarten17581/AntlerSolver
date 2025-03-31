package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;

public class KPathAntlerStrategy implements KernalizationStrategy {

    public final int k;
    public final boolean onlyLengthCheck;

    public KPathAntlerStrategy(int k, boolean onlyLengthCheck) {
        this.k = k;
        this.onlyLengthCheck = onlyLengthCheck;
    }

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Set<Node> toBeRemoved = new HashSet<>();
        List<PathAntler> pathAntlers = GraphAlgorithm.getKPathAntlers(k, graph, onlyLengthCheck);
        for(PathAntler pathAntler : pathAntlers) {
            if(pathAntler.getA().isEmpty()) continue;
            for(Node a : pathAntler.getA()) {
                if(toBeRemoved.contains(a)) continue;
                RemoveNodeCommand removeA = new RemoveNodeCommand(a.id, graph);
                command.commands.add(removeA);
                solutionSet.add(a);
            }
            toBeRemoved.addAll(pathAntler.getP());
            toBeRemoved.addAll(pathAntler.getC());
        }
        command.execute();
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        KernalizationStrategy requirementStrategy = new CompositeKernalizationStrategy(new KernalizationStrategy[]{
            new IsolatedStrategy(),
            new LeafStrategy(),
            new Degree2Strategy(),
            new MultiEdgeStrategy(),
            new SelfloopStrategy(),
        });
        while(true) {
            Pair<Command, List<Node>> requirementPair = requirementStrategy.exhaustiveApply(graph);
            if(requirementPair != null) {
                command.commands.add(requirementPair.a);
                solutionSet.addAll(requirementPair.b);
            }
            Pair<Command, List<Node>> pair = apply(graph);
            if(pair == null) {
                break;
            }
            command.commands.add(pair.a);
            solutionSet.addAll(pair.b);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command,List<Node>>(command, solutionSet);
    }
}
