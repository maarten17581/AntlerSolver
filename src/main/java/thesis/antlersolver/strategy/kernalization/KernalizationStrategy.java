package thesis.antlersolver.strategy.kernalization;

import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public interface KernalizationStrategy {
    public Pair<Command, List<Node>> apply(Graph graph);
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph);
}
