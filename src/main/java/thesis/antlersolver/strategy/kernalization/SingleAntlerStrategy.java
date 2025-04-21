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

public class SingleAntlerStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Set<Node> toBeRemoved = new HashSet<>();
        for(Node v : graph.singleAntler) {
            long time = -System.currentTimeMillis();
            if(toBeRemoved.contains(v)) continue;
            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
            Edge[] neighbors = v.neighbors.values().toArray(new Edge[0]);
            Node w = neighbors[0].c > neighbors[1].c ? neighbors[0].t : neighbors[1].t;
            if(toBeRemoved.contains(w)) continue;
            RemoveNodeCommand removeW = new RemoveNodeCommand(w.id, graph);
            toBeRemoved.add(v);
            toBeRemoved.add(w);
            command.commands.add(removeV);
            command.commands.add(removeW);
            solutionSet.add(w);
            time += System.currentTimeMillis();
            Statistics.getStat().count("1Antler");
            Statistics.getStat().count("1AntlerSize");
            //Statistics.getStat().count("1AntlerTime", time);
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
        while(!graph.singleAntler.isEmpty()) {
            long time = -System.currentTimeMillis();
            Node v = graph.singleAntler.iterator().next();
            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
            Edge[] neighbors = v.neighbors.values().toArray(new Edge[0]);
            Node w = neighbors[0].c > neighbors[1].c ? neighbors[0].t : neighbors[1].t;
            RemoveNodeCommand removeW = new RemoveNodeCommand(w.id, graph);
            removeV.execute();
            removeW.execute();
            command.commands.add(removeV);
            command.commands.add(removeW);
            solutionSet.add(w);
            time += System.currentTimeMillis();
            Statistics.getStat().count("1Antler");
            Statistics.getStat().count("1AntlerSize");
            //Statistics.getStat().count("1AntlerTime", time);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }
}
