package thesis.antlersolver.strategy.branching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class NaiveBranchingStrategy implements BranchingStrategy {
    
    public Pair<Command, List<Node>> apply(Graph graph) {
        int id = graph.nodes.keySet().iterator().next();
        Command command = new RemoveNodeCommand(id, graph);
        command.execute();
        return new Pair<Command, List<Node>>(command, Collections.singletonList(graph.nodes.get(id)));
    }
}
