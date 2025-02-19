package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class MultiEdgeStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        for(Edge e : graph.multiEdge) {
            RemoveEdgeCommand removeE = new RemoveEdgeCommand(e.s.id, e.t.id, e.c-2, graph);
            command.commands.add(removeE);
        }
        command.execute();
        return new Pair<Command, List<Node>>(command, new ArrayList<>());
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        return apply(graph);
    }
}
