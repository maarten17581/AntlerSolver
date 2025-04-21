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
import thesis.antlersolver.statistics.Statistics;

public class SingletonPathAntlerStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Set<Node> toBeRemoved = new HashSet<>();
        long computeTime = -System.currentTimeMillis();
        List<PathAntler> pathAntlers = GraphAlgorithm.getSingletonPathAntlers(graph, true);
        computeTime += System.currentTimeMillis();
        Statistics.getStat().count("1PathAntlerComputed");
        Statistics.getStat().count("1PathAntlerComputeTime", computeTime);
        for(PathAntler pathAntler : pathAntlers) {
            long time = -System.currentTimeMillis();
            if(pathAntler.getA().isEmpty()) continue;
            Node a = pathAntler.getA().iterator().next();
            if(toBeRemoved.contains(a)) continue;
            toBeRemoved.add(a);
            toBeRemoved.addAll(pathAntler.getP());
            RemoveNodeCommand removeA = new RemoveNodeCommand(a.id, graph);
            command.commands.add(removeA);
            solutionSet.add(a);
            time += System.currentTimeMillis();
            Statistics.getStat().count("1PathAntler");
            Statistics.getStat().count("1PathAntlerSize");
            //Statistics.getStat().count("1PathAntlerTime", time);
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
            long time = -System.currentTimeMillis();
            Pair<Command, List<Node>> pair = apply(graph);
            if(pair == null) {
                break;
            }
            command.commands.add(pair.a);
            solutionSet.addAll(pair.b);
            time += System.currentTimeMillis();
            Statistics.getStat().count("1PathAntler");
            Statistics.getStat().count("1PathAntlerSize");
            //Statistics.getStat().count("1PathAntlerTime", time);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command,List<Node>>(command, solutionSet);
    }
}
