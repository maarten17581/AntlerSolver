package thesis.antlersolver.strategy.kernalization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.CompositeCommand;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.model.Edge;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;

public class SingletonPathAntlerStrategy implements KernalizationStrategy {

    @Override
    public Pair<Command, List<Node>> apply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        Set<Node> toBeRemoved = new HashSet<>();
        List<PathAntler> pathAntlers = GraphAlgorithm.getSingletonPathAntlers(graph);
        for(PathAntler pathAntler : pathAntlers) {
            if(toBeRemoved.contains(pathAntler.C.get(0))) continue;
            if(pathAntler.P.size() <= 1) continue;
            if(pathAntler.P.size() == 2 && !(pathAntler.C.get(0).neighbors.get(pathAntler.P.get(0)).c >= 2 && pathAntler.C.get(0).neighbors.get(pathAntler.P.get(1)).c >= 2)) continue;
            if(pathAntler.P.size() == 3) {
                boolean extraCycle = false;
                for(Node v : pathAntler.P) {
                    boolean ismiddle = true;
                    for(Node w : pathAntler.P) {
                        if(v != w && w.neighbors.get(v) == null) {
                            ismiddle = false;
                            break;
                        }
                    }
                    if(ismiddle) {
                        for(Node w : pathAntler.P) {
                            if(v != w && w.neighbors.get(pathAntler.C.get(0)).c >= 2) {
                                extraCycle = true;
                            }
                        }
                        break;
                    }
                }
                if(!extraCycle) {
                    continue;
                }
            }

            RemoveNodeCommand removeV = new RemoveNodeCommand(pathAntler.C.get(0).id, graph);
            command.commands.add(removeV);
            solutionSet.add(pathAntler.C.get(0));
        }
        command.execute();
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }

    @Override
    public Pair<Command, List<Node>> exhaustiveApply(Graph graph) {
        CompositeCommand command = new CompositeCommand();
        List<Node> solutionSet = new ArrayList<>();
        while(true) {
            Pair<Command, List<Node>> singleTry = apply(graph);
            if(singleTry == null) break;
            command.commands.add(singleTry.a);
            solutionSet.addAll(singleTry.b);
        }
        command.executed = true;
        if(command.commands.isEmpty()) {
            return null;
        }
        return new Pair<Command, List<Node>>(command, solutionSet);
    }
}
