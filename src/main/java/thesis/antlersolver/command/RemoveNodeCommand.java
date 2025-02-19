package thesis.antlersolver.command;

import java.util.Map;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Edge;

public class RemoveNodeCommand implements Command {
    public boolean executed = false;
    public int id;
    public Graph graph;
    public Node n;
    public CompositeCommand edges;

    public RemoveNodeCommand(int id, Graph graph) {
        this.id = id;
        this.graph = graph;
        edges = new CompositeCommand();
    }

    @Override
    public void execute() {
        n = graph.nodes.get(id);
        for(Map.Entry<Node, Edge> entry : n.neighbors.entrySet()) {
            RemoveEdgeCommand command = new RemoveEdgeCommand(id, entry.getKey().id, entry.getValue().c, graph);
            edges.commands.add(command);
        }
        edges.execute();
        graph.removeNode(id);
        executed = true;
    }

    @Override
    public void undo() {
        graph.addNode(id);
        edges.undo();
        edges.commands.clear();
        executed = false;
    }
}
