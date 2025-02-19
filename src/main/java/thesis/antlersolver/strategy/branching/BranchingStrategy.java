package thesis.antlersolver.strategy.branching;

import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public interface BranchingStrategy {
    public Pair<Command, List<Node>> apply(Graph graph);
}
