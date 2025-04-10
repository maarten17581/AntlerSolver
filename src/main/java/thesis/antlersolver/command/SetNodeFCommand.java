package thesis.antlersolver.command;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;

public class SetNodeFCommand implements Command {
    public boolean executed = false;
    public int id;
    public Graph graph;
    public Node v;

    public SetNodeFCommand(int id, Graph graph) {
        this.id = id;
        this.graph = graph;
    }

    @Override
    public void execute() {
        v = graph.nodes.get(id);
        graph.addToF(v);
        executed = true;
    }

    @Override
    public void undo() {
        graph.removeFromF(v);
        executed = false;
    }
}
