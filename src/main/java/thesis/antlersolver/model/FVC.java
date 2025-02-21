package thesis.antlersolver.model;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.RemoveNodeCommand;

public class FVC {
    public List<Node> A;
    public List<Node> C;
    public List<Node> F;
    public Graph graph;

    public FVC(Graph graph) {
        this.graph = graph;
        A = new ArrayList<>();
        C = new ArrayList<>();
        F = new ArrayList<>();
    }

    public void setMaxF() {
        List<Command> commands = new ArrayList<>();
        for(Node v : C) {
            Command command = new RemoveNodeCommand(v.id, graph);
            command.execute();
            commands.add(command);
        }
        while(!graph.leaves.isEmpty()) {
            Node v = graph.leaves.iterator().next();
            F.add(v);
            Command command = new RemoveNodeCommand(v.id, graph);
            command.execute();
            commands.add(command);
        }
        while(!graph.isolated.isEmpty()) {
            Node v = graph.isolated.iterator().next();
            F.add(v);
            Command command = new RemoveNodeCommand(v.id, graph);
            command.execute();
            commands.add(command);
        }
        for(int i = commands.size()-1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
}
