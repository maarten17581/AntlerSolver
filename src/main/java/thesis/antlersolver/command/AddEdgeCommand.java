package thesis.antlersolver.command;

import thesis.antlersolver.model.Graph;

public class AddEdgeCommand implements Command {
    public boolean executed = false;
    public int sid;
    public int tid;
    public int c;
    public Graph graph;

    public AddEdgeCommand(int sid, int tid, int c, Graph graph) {
        this.sid = sid;
        this.tid = tid;
        this.c = c;
        this.graph = graph;
    }

    public AddEdgeCommand(int sid, int tid, Graph graph) {
        this.sid = sid;
        this.tid = tid;
        this.c = 1;
        this.graph = graph;
    }

    @Override
    public void execute() {
        graph.addEdge(sid, tid, c);
        executed = true;
    }

    @Override
    public void undo() {
        graph.removeEdge(sid, tid, c);
        executed = true;
    }
}
