package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.statistics.Statistics;

public class ContractFStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        while(!graph.betweenF.isEmpty()) {
            long time = -System.currentTimeMillis();
            Edge e = graph.betweenF.iterator().next();
            for(Edge e2 : e.t.neighbors.values()) {
                if(e2.t == e.s) continue;
                AddEdgeCommand addE = new AddEdgeCommand(e.s.id, e2.t.id, e2.c, graph);
                addE.execute();
                command.commands.add(addE);
            }
            RemoveNodeCommand removeV = new RemoveNodeCommand(e.t.id, graph);
            removeV.execute();
            command.commands.add(removeV);
            time += System.currentTimeMillis();
            Statistics.getStat().count("ContractF");
            //Statistics.getStat().count("ContractFTime", time);
        }
        command.executed = true;
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
