package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class SelfloopStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        for(Node v : graph.selfloop) {
            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
            solutionSet.add(v);
            command.commands.add(removeV);
        }
        command.execute();
        return new Pair<Command, List<Node>>(command, solutionSet);
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        return apply(graph);
    }
}
