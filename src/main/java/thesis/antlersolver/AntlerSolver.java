package thesis.antlersolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import fvs_wata_orz.FPTBranchingSolver;
import fvs_wata_orz.Graph;
import fvs_wata_orz.ReductionRoot;
import fvs_wata_orz.tc.wata.debug.Debug;
import thesis.antlersolver.io.FileReader;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.reductions.AntlerReduction;
import thesis.antlersolver.solvers.AntlerReductionSolver;
import thesis.antlersolver.solvers.TimedSolve;
import thesis.antlersolver.statistics.Statistics;

public class AntlerSolver {
    public static void main(String[] args) {

        if (args.length != 2) {
			printHelp();
			return;
		}

        int counter = 0;
        long cummulativeTime = 0;
        final long maxTime = 60*1000;
        long totalTimer = -System.currentTimeMillis();
        ReductionRoot.DEBUG = false;
        //Debug.silent = true;
        StringBuilder sb = new StringBuilder();
        sb.append("graph,n,m,n\',m\',n\",m\",fvs,time\n");
        try {
            File input = new File(args[0]);
            Pair<Graph[], String[]> graphPairs = new Pair<Graph[], String[]>(new Graph[1], new String[1]);
            if(input.isDirectory()) {
                graphPairs = FileReader.readGraphDir(args[0]);
            } else {
                Pair<Graph, String> graph = FileReader.readGraph(args[0]);
                graphPairs.a[0] = graph.a;
                graphPairs.b[0] = graph.b;
            }
            Graph[] graphs = graphPairs.a;
            String[] names = graphPairs.b;
            Integer[] sortIndices = new Integer[graphs.length];
            for(int i = 0; i < graphs.length; i++) sortIndices[i] = i;
            // Arrays.sort(sortIndices, (i1, i2) -> 
            //     graphs[i1].n() == graphs[i2].n() ? graphs[i1].m()-graphs[i2].m() : graphs[i1].n()-graphs[i2].n());
            for(int gCount = 0; gCount < graphs.length; gCount++) {
                int g = sortIndices[gCount];
                Graph graph = graphs[g];
                // Linear time kernelization
                int originalsizeN = graph.n();
                int originalsizeM = graph.m();
                long time = -System.currentTimeMillis();
                if(true) {
                    ReductionRoot.reduce(graph);
                }
                int kernelsizeN = graph.n();
                int kernelsizeM = graph.m();
                // antler reduction check
                if(true) {
                    int last = graph.n();
                    do {
                        last = graph.n();
                        AntlerReduction.reduce(graph);
                    } while(graph.n() != last);
                }
                System.out.println("graph "+names[g]+" reduction: ("+originalsizeN+", "+originalsizeM+") -> (" +
                        kernelsizeN+", "+kernelsizeM+") -> ("+graph.n()+", "+graph.m()+")");
                sb.append(names[g]+","+originalsizeN+","+originalsizeM+","
                        +kernelsizeN+","+kernelsizeM+","+graph.n()+","+graph.m()+",");
                Statistics.getStat().print(args[1]+"/"+names[g]+"Reduction.txt");
                Statistics.reset();
                TimedSolve solver = new TimedSolve(new AntlerReductionSolver());
                int[] fvs = solver.solve(graph, maxTime-(time+System.currentTimeMillis()));
                if(fvs == null) {
                    System.out.println("graph "+names[g]+" failed to compute");
                    System.out.println(counter+"/"+(gCount+1)+"/"+graphs.length);
                    Statistics.getStat().print(args[1]+"/"+names[g]+"Branching.txt");
                    Statistics.reset();
                    sb.append(",\n");
                    continue;
                }
                time += System.currentTimeMillis();
                cummulativeTime += time;
                String padding1 = "";
                String padding2 = "";
                for(int i = names[g].length(); i < 50; i++) {
                    padding1 += " ";
                }
                for(int i = (fvs.length+"").length(); i < 50; i++) {
                    padding2 += " ";
                }
                counter++;
                System.out.println("graph: "+names[g]+padding1+"fvs size: "+fvs.length+padding2+"time: "+time+" ms");
                System.out.println(counter+"/"+(gCount+1)+"/"+graphs.length);
                Statistics.getStat().print(args[1]+"/"+names[g]+"Branching.txt");
                Statistics.reset();
                sb.append(fvs.length+","+time+"\n");
            }

        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
        totalTimer += System.currentTimeMillis();
        System.out.println(counter+" graphs solved in "+cummulativeTime+" ms");
        System.out.println("Total running time of "+totalTimer+" ms");
        try {
            FileWriter fw = new FileWriter(new File(args[1]+"/statistics.csv"));
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
