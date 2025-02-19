package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.AddEdgeCommand;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class Degree2Strategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        while(!graph.degree2.isEmpty()) {
            Node v = graph.degree2.iterator().next();
            RemoveNodeCommand removeV = new RemoveNodeCommand(graph.degree2.iterator().next().id, graph);
            Node[] neighbors = v.neighbors.keySet().toArray(new Node[0]);
            AddEdgeCommand edgeNbh = new AddEdgeCommand(neighbors[0].id, (v.nbhSize > 1 ? neighbors[1] : neighbors[0]).id, graph);
            removeV.execute();
            edgeNbh.execute();
            command.commands.add(removeV);
            command.commands.add(edgeNbh);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, new ArrayList<>());
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        return apply(graph);
    }
}
