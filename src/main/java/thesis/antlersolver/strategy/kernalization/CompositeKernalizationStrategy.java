package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.List;

import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;

public class CompositeKernalizationStrategy implements KernalizationStrategy {

    KernalizationStrategy[] strategies;

    public CompositeKernalizationStrategy(KernalizationStrategy[] strategies) {
        this.strategies = strategies;
    }

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        for(KernalizationStrategy strategy : strategies) {
            Pair<Command, List<Node>> pair = strategy.apply(graph);
            if(pair == null) {
                continue;
            }
            command.commands.add(pair.a);
            solutionSet.addAll(pair.b);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        int nodecount = graph.nodecount;
        int edgecount = graph.edgecount;
        while(true) {
            for(KernalizationStrategy strategy : strategies) {
                Pair<Command, List<Node>> pair = strategy.exhaustiveApply(graph);
                if(pair == null) {
                    continue;
                }
                command.commands.add(pair.a);
                solutionSet.addAll(pair.b);
            }
            if(nodecount == graph.nodecount && edgecount == graph.edgecount) {
                break;
            }
            nodecount = graph.nodecount;
            edgecount = graph.edgecount;
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }
}
