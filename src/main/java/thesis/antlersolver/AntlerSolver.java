package thesis.antlersolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.command.Command;
import thesis.antlersolver.command.RemoveNodeCommand;
import thesis.antlersolver.io.FileReader;
import thesis.antlersolver.model.FVC;
import thesis.antlersolver.model.Graph;
import thesis.antlersolver.model.Node;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.model.PathAntler;
import thesis.antlersolver.strategy.branching.BranchingStrategy;
import thesis.antlersolver.strategy.branching.NaiveBranchingStrategy;
import thesis.antlersolver.strategy.kernalization.CompositeKernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.Degree2Strategy;
import thesis.antlersolver.strategy.kernalization.EdgeBCCStrategy;
import thesis.antlersolver.strategy.kernalization.IsolatedStrategy;
import thesis.antlersolver.strategy.kernalization.KAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.KPathAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.KernalizationStrategy;
import thesis.antlersolver.strategy.kernalization.LeafStrategy;
import thesis.antlersolver.strategy.kernalization.MultiEdgeStrategy;
import thesis.antlersolver.strategy.kernalization.SelfloopStrategy;
import thesis.antlersolver.strategy.kernalization.SingleAntlerStrategy;
import thesis.antlersolver.strategy.kernalization.SingletonPathAntlerStrategy;

public class AntlerSolver {
    public static void main(String[] args) {

        if (args.length != 2) {
			printHelp();
			return;
		}

        KernalizationStrategy teststrategy = new CompositeKernalizationStrategy(new KernalizationStrategy[]{
            new IsolatedStrategy(),
            new LeafStrategy(),
            new Degree2Strategy(),
            new MultiEdgeStrategy(),
            new SelfloopStrategy(),
            new EdgeBCCStrategy(),
            new SingleAntlerStrategy(),
            new SingletonPathAntlerStrategy()
        });

        KernalizationStrategy strategy = new CompositeKernalizationStrategy(new KernalizationStrategy[]{
            new IsolatedStrategy(),
            new LeafStrategy(),
            new Degree2Strategy(),
            new MultiEdgeStrategy(),
            new SelfloopStrategy(),
            new EdgeBCCStrategy(),
            new SingleAntlerStrategy(),
            new SingletonPathAntlerStrategy(),
            new KPathAntlerStrategy(2, true),
            new KAntlerStrategy(2),
        });
        BranchingStrategy branchingStrategy = new NaiveBranchingStrategy();

        int counter = 0;
        long cummulativeTime = 0;
        
        try {
            File input = new File(args[0]);
            Graph[] graphs = new Graph[1];
            if(input.isDirectory()) {
                graphs = FileReader.readGraphDir(args[0]);
            } else {
                graphs[0] = FileReader.readGraph(args[0]);
            }
            Arrays.sort(graphs, (Graph g1, Graph g2) -> 
                g1.nodecount == g2.nodecount ? g1.edgecount-g2.edgecount : g1.nodecount-g2.nodecount);
            for(Graph graph : graphs) {
                //if(graph.nodecount > 150 || graph.edgecount > 500) continue;
                //long testtime = -System.currentTimeMillis();
                //List<Node> testset = GraphAlgorithm.smartFVS(graph);
                //testtime += System.currentTimeMillis();
                long time = -System.currentTimeMillis();
                System.out.println(graph.name);
                List<Node> solutionSet = new ArrayList<>();
                Pair<Command, List<Node>> startKernel = strategy.exhaustiveApply(graph);
                if(startKernel != null) {
                    solutionSet.addAll(startKernel.b);
                }
                // List<PathAntler> pathAntlers2 = GraphAlgorithm.getKPathAntlers(2, graph, true);
                // System.out.println("Number of 2 path anthers "+pathAntlers2.size()+". Are empty: "+(pathAntlers2.isEmpty() ? "-" : (pathAntlers2.get(0).getA().isEmpty())));
                // List<PathAntler> pathAntlers3 = GraphAlgorithm.getKPathAntlers(3, graph, true);
                // System.out.println("Number of 3 path anthers "+pathAntlers3.size()+". Are empty: "+(pathAntlers3.isEmpty() ? "-" : (pathAntlers3.get(0).getA().isEmpty())));
                // List<PathAntler> pathAntlers4 = GraphAlgorithm.getKPathAntlers(4, graph, true);
                // System.out.println("Number of 4 path anthers "+pathAntlers4.size()+". Are empty: "+(pathAntlers4.isEmpty() ? "-" : (pathAntlers4.get(0).getA().isEmpty())));
                List<Graph> graphComponents = GraphAlgorithm.connectedComponentsGraph(graph);
                List<List<Node>> cc = GraphAlgorithm.connectedComponents(graph);
                int maxsize = 0;
                for(List<Node> component : cc) {
                    maxsize = Math.max(maxsize, component.size());
                }
                if(maxsize <= 100) {
                    for(Graph graphComponent : graphComponents) {
                        List<Node> subFVS = GraphAlgorithm.smartFVS(graphComponent);
                        solutionSet.addAll(subFVS);
                        for(Node v : subFVS) {
                            RemoveNodeCommand removeV = new RemoveNodeCommand(v.id, graph);
                            removeV.execute();
                        }
                    }
                }
                // while(!GraphAlgorithm.isAcyclic(graph)) {
                //     Pair<Command, List<Node>> branch = branchingStrategy.apply(graph);
                //     Pair<Command, List<Node>> kernel = strategy.exhaustiveApply(graph);
                //     if(branch != null) {
                //         solutionSet.addAll(branch.b);
                //     }
                //     if(kernel != null) {
                //         solutionSet.addAll(kernel.b);
                //     }
                // }
                // System.out.println("After, Nodes: " + graph.nodecount + ", Edges: " + graph.edgecount + ", FVS size: " + solutionSet.size());
                // System.out.println("Number of CC: " + GraphAlgorithm.connectedComponents(graph).size() + ", Is FVS solved: " + GraphAlgorithm.isAcyclic(graph));
                time += System.currentTimeMillis();
                System.out.println("Time: "+time);
                if(GraphAlgorithm.isAcyclic(graph)) {
                    counter++;
                    System.out.println(counter + ": " + graph.name + ", FVS size: " + solutionSet.size() + ", CC: " + cc.size() + ", Max CC size: " + maxsize);
                    // if(solutionSet.size() != testset.size()) {
                    //     System.out.println("Graph: "+graph.name+" is wrong, our fvs size: "+solutionSet.size() + ", actual fvs size: "+testset.size());
                    // }
                    // cummulativeTime += testtime-time;
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(counter+" graphs solved, "+cummulativeTime+" ms faster");
    }

    private static void printHelp() {
		System.out.println("AntlerSolver - an application that implements a new algorithm for\n" +
				"solving the feedback vertex set problem on multigraphs\n" +
				"\n" +
				"Usage: java -jar Antlersolver.jar <graphFile/graphDir> <outputDir>\n" +
				"\n" +
				"graphFile/graphDir can be any .edges file or directory containing .edges files.\n" +
				"Each .edges file consists of lines which are either comments (starting with # or %),\n" +
				"or 2 space seperated numbers x y representing an edge between node x and node y\n" +
				"\n" +
				"outputDir is the directory where files for the minimum FVS per .edges file will be stored.");
	}
}
