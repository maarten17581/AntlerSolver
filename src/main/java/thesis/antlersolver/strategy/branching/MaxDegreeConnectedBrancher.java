package thesis.antlersolver.strategy.branching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.command.SetNodeFCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.statistics.Statistics;

public class MaxDegreeConnectedBrancher implements Brancher {

    public Graph graph;
    public int state;
    public Node v;
    public Command command;

    public MaxDegreeConnectedBrancher(Graph graph) {
        this.graph = graph;
        state = 0;
        Node isF = null;
        for(Node u : graph.nodes.values()) {
            if(!u.isF() && (v == null || u.degree > v.degree)) v = u;
            if(u.isF()) isF = u;
        }
        if(isF != null) {
            v = null;
            for(Node u : isF.neighbors.keySet()) {
                if(v == null || u.degree > v.degree) v = u;
            }
        }
    }

    public List<Node> next() {
        Statistics.getStat().count("Branch");
        if(command != null) {
            command.undo();
            command = null;
        }
        if(state == 0) {
            command = new SetNodeFCommand(v.id, graph);
            command.execute();
            state++;
            return new ArrayList<>();
        } else if(state == 1) {
            command = new RemoveNodeCommand(v.id, graph);
            command.execute();
            state++;
            return Arrays.asList(new Node[]{v});
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
