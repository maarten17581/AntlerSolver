package thesis.antlersolver.strategy.branching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.command.SetNodeFCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;

public class NaiveBrancher implements Brancher {

    public Graph graph;
    public int state;
    public Node v;
    public Command command;

    public NaiveBrancher(Graph graph) {
        this.graph = graph;
        state = 0;
        Iterator<Node> it = graph.nodes.values().iterator();
        while(it.hasNext()) {
            v = it.next();
            if(!v.isF()) {
                break;
            }
        }
    }

    public List<Node> next() {
        if(command != null) {
            command.undo();
            command = null;
        }
        if(state == 0) {
            command = new RemoveNodeCommand(v.id, graph);
            command.execute();
            state++;
            return Arrays.asList(new Node[]{v});
        } else if(state == 1) {
            command = new SetNodeFCommand(v.id, graph);
            command.execute();
            state++;
            return new ArrayList<>();
        } else {
            return null;
        }
    }

    public boolean hasNext() {
        return state <= 1;
    }

    public void reset() {
        if(command != null) {
            command.undo();
            command = null;
        }
        state = 0;
    }
}
