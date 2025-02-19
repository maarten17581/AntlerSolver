package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class LeafStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        for(Node v : graph.leaves) {
            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
            command.commands.add(removeV);
        }
        command.execute();
        return new Pair<Command, List<Node>>(command, new ArrayList<>());
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        while(!graph.leaves.isEmpty()) {
            RemoveNodeCommand removeV = new RemoveNodeCommand(graph.leaves.iterator().next().id, graph);
            removeV.execute();
            command.commands.add(removeV);
        }
        command.executed = true;
        return new Pair<Command, List<Node>>(command, new ArrayList<>());
    }
}
