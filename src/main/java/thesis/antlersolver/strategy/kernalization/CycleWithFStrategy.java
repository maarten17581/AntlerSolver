package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.statistics.Statistics;

public class CycleWithFStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Set<Node> removedNodes = new HashSet<>();
        for(Edge e : graph.doubleToF) {
            long time = -System.currentTimeMillis();
            Node v = e.s.isF() ? e.t : e.s;
            if(removedNodes.contains(v)) continue;
            solutionSet.add(v);
            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
            removedNodes.add(v);
            command.commands.add(removeV);
            time += System.currentTimeMillis();
            Statistics.getStat().count("CycleWithF");
            //Statistics.getStat().count("CycleWithFTime", time);
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
        while(!graph.doubleToF.isEmpty()) {
            long time = -System.currentTimeMillis();
            Edge e = graph.doubleToF.iterator().next();
            Node v = e.s.isF() ? e.t : e.s;
            solutionSet.add(v);
            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
            removeV.execute();
            command.commands.add(removeV);
            time += System.currentTimeMillis();
            Statistics.getStat().count("CycleWithF");
            //Statistics.getStat().count("CycleWithFTime", time);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }
}
