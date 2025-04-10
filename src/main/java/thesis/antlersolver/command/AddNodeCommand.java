package thesis.antlersolver.command;

import thesis.antlersolver.model.Graph;

public class AddNodeCommand implements Command {
    public boolean executed = false;
    public int id;
    public Graph graph;

    public AddNodeCommand(int id, Graph graph) {
        this.id = id;
        this.graph = graph;
    }

    public AddNodeCommand(Graph graph) {
        id = -1;
        this.graph = graph;
    }

    @Override
    public void execute() {
        if(id == -1) {
            id = graph.addNode().id;
        } else {
            graph.addNode(id);
        }
        executed = true;
    }

    @Override
    public void undo() {
        graph.removeNode(id);
        executed = false;
    }
}
