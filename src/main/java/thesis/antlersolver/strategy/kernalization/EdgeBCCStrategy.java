package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.statistics.Statistics;

public class EdgeBCCStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        long computeTime = -System.currentTimeMillis();
        List<Edge> bridges = GraphAlgorithm.edgeBCC(graph);
        computeTime += System.currentTimeMillis();
        Statistics.getStat().count("EdgeBCCComputed");
        Statistics.getStat().count("EdgeBCCComputeTime", computeTime);
        for(Edge e : bridges) {
            long time = -System.currentTimeMillis();
            RemoveEdgeCommand removeE = new RemoveEdgeCommand(e.s.id, e.t.id, e.c, graph);
            command.commands.add(removeE);
            time += System.currentTimeMillis();
            Statistics.getStat().count("Bridge");
            //Statistics.getStat().count("BridgeTime", time);
        }
        command.execute();
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, new ArrayList<>());
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        return apply(graph);
    }
}
