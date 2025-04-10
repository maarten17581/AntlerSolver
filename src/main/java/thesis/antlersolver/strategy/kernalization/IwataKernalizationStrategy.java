package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveEdgeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.command.SetNodeFCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class IwataKernalizationStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        int[][] adj = new int[graph.nodeIds][0];
        for(Node v : graph.nodes.values()) {
            adj[v.id] = new int[v.degree];
            int index = 0;
            for(Edge e : v.neighbors.values()) {
                for(int i = 0; i < e.c; i++) {
                    adj[v.id][index] = e.t.id;
                    index++;
                }
            }
            Arrays.sort(adj[v.id]);
        }
        fvs_wata_orz.Graph fvsGraph = new fvs_wata_orz.Graph(adj);
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        fvs_wata_orz.ReductionRoot.reduce(fvsGraph);
        List<Node> subsolution = new ArrayList<>();
        CompositeCommand command = new CompositeCommand();
        for(int i = 0; i < fvsGraph.n; i++) {
            if(fvsGraph.used[i] == 'S') {
                subsolution.add(graph.nodes.get(i));
            } else if(fvsGraph.used[i] == 'F') {
                SetNodeFCommand setF = new SetNodeFCommand(i, graph);
                setF.execute();
                command.commands.add(setF);
            }
            if(fvsGraph.adj[i].length == 0) {
                RemoveNodeCommand removeV = new RemoveNodeCommand(i, graph);
                removeV.execute();
                command.commands.add(removeV);
            } else {
                CompositeCommand removeEdges = new CompositeCommand();
                for(Edge e : graph.nodes.get(i).neighbors.values()) {
                    RemoveEdgeCommand removeE = new RemoveEdgeCommand(i, e.t.id, e.c, graph);
                    removeEdges.commands.add(removeE);
                }
                removeEdges.execute();
                command.commands.add(removeEdges);
            }
        }
        for(int i = 0; i < fvsGraph.n; i++) {
            if(fvsGraph.adj[i].length > 0) {
                for(int j : fvsGraph.adj[i]) {
                    if(j >= i) {
                        AddEdgeCommand addE = new AddEdgeCommand(i, j, graph);
                        addE.execute();
                        command.commands.add(addE);
                    }
                }
            }
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, subsolution);
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        return apply(graph);
    }
}
