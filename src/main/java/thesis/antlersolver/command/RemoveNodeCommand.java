package thesis.antlersolver.command;

import java.util.Map;

import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Edge;

public class RemoveNodeCommand implements Command {
    public boolean executed = false;
    public int id;
    public Graph graph;
    public Node v;
    public boolean isF;
    public CompositeCommand edges;

    public RemoveNodeCommand(int id, Graph graph) {
        this.id = id;
        this.graph = graph;
    }

    @Override
    public void execute() {
        v = graph.nodes.get(id);
        isF = v.isF();
        if(edges == null) {
            edges = new CompositeCommand();
            for(Map.Entry<Node, Edge> entry : v.neighbors.entrySet()) {
                RemoveEdgeCommand command = new RemoveEdgeCommand(id, entry.getKey().id, entry.getValue().c, graph);
                edges.commands.add(command);
            }
            edges.execute();
        } else {
            edges.execute();
        }
        graph.removeNode(id);
        executed = true;
    }

    @Override
    public void undo() {
        graph.addNode(v);
        if(isF) {
            graph.addToF(v);
        } else {
            graph.removeFromF(v);
        }
        edges.undo();
        edges.commands.clear();
        executed = false;
    }
}
