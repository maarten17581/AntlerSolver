package thesis.antlersolver.strategy.branching;

import java.util.List;

import thesis.antlersolver.model.Node;

public interface Brancher {
    public List<Node> next();
    public boolean hasNext();
    public void reset();
}
