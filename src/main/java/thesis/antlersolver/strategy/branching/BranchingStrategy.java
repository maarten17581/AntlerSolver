package thesis.antlersolver.strategy.branching;

import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;

public interface BranchingStrategy {
    public Command apply(Graph graph);
    public List<Node> getSolutionSet();
    public int getSolutionSetSize();
}
