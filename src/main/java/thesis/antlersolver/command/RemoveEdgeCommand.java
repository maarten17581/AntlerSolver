package thesis.antlersolver.command;

import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;

public class RemoveEdgeCommand implements Command {
    public boolean executed = false;
    public int sid;
    public int tid;
    public int c;
    public Edge e;
    public Graph graph;

    public RemoveEdgeCommand(int sid, int tid, int c, Graph graph) {
        this.sid = sid;
        this.tid = tid;
        this.c = c;
        this.graph = graph;
    }

    public RemoveEdgeCommand(int sid, int tid, Graph graph) {
        this(sid, tid, 1, graph);
    }

    @Override
    public void execute() {
        e = graph.nodes.get(sid).neighbors.get(graph.nodes.get(tid));
        graph.removeEdge(sid, tid, c);
        executed = true;
    }

    @Override
    public void undo() {
        graph.addEdge(sid, tid, c);
        executed = true;
    }
}
