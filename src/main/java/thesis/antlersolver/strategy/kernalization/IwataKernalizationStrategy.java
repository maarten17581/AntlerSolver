package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import thesis.antlersolver.statistics.Statistics;

public class IwataKernalizationStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        long time = -System.currentTimeMillis();
        Map<Node, Integer> map = new HashMap<>();
        Map<Integer, Node> mapBack = new HashMap<>();
        for(Node v : graph.nodes.values()) {
            map.put(v, map.size());
            mapBack.put(map.get(v), v);
        }
        int[][] adj = new int[map.size()][0];
        for(Node v : graph.nodes.values()) {
            adj[map.get(v)] = new int[v.degree];
            int index = 0;
            for(Edge e : v.neighbors.values()) {
                for(int i = 0; i < e.c; i++) {
                    adj[map.get(v)][index] = map.get(e.t);
                    index++;
                }
            }
            Arrays.sort(adj[map.get(v)]);
        }
        fvs_wata_orz.Graph fvsGraph = new fvs_wata_orz.Graph(adj);
        fvs_wata_orz.ReductionRoot.DEBUG = false;
        fvs_wata_orz.ReductionRoot.reduce(fvsGraph);
        List<Node> subsolution = new ArrayList<>();
        CompositeCommand command = new CompositeCommand();
        for(int i = 0; i < fvsGraph.n; i++) {
            if(fvsGraph.used[i] == 'S') {
                subsolution.add(mapBack.get(i));
            } else if(fvsGraph.used[i] == 'F') {
                SetNodeFCommand setF = new SetNodeFCommand(mapBack.get(i).id, graph);
                setF.execute();
                command.commands.add(setF);
            }
            if(fvsGraph.adj[i].length == 0) {
                RemoveNodeCommand removeV = new RemoveNodeCommand(mapBack.get(i).id, graph);
                removeV.execute();
                command.commands.add(removeV);
            } else {
                CompositeCommand removeEdges = new CompositeCommand();
                for(Edge e : mapBack.get(i).neighbors.values()) {
                    RemoveEdgeCommand removeE = new RemoveEdgeCommand(e.s.id, e.t.id, e.c, graph);
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
                        AddEdgeCommand addE = new AddEdgeCommand(mapBack.get(i).id, mapBack.get(j).id, graph);
                        addE.execute();
                        command.commands.add(addE);
                    }
                }
            }
        }
        command.executed = true;
        time += System.currentTimeMillis();
        Statistics.getStat().count("IwataKernel");
        Statistics.getStat().count("IwataKernelTime", time);
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
