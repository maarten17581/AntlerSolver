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

public class SingletonPathAntlerStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Set<Node> toBeRemoved = new HashSet<>();
        List<PathAntler> pathAntlers = GraphAlgorithm.getSingletonPathAntlers(graph, true);
        for(PathAntler pathAntler : pathAntlers) {
            if(pathAntler.getA().isEmpty()) continue;
            Node a = pathAntler.getA().iterator().next();
            if(toBeRemoved.contains(a)) continue;
            toBeRemoved.add(a);
            toBeRemoved.addAll(pathAntler.getP());
            RemoveNodeCommand removeA = new RemoveNodeCommand(a.id, graph);
            command.commands.add(removeA);
            solutionSet.add(a);
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
        while(true) {
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
