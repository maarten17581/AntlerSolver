package thesis.antlersolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fvs_wata_orz.Graph;
import fvs_wata_orz.ReductionRoot;
import fvs_wata_orz.tc.wata.debug.Debug;
import thesis.antlersolver.algorithm.GraphAlgorithm;
import thesis.antlersolver.io.FileReader;
import thesis.antlersolver.model.Pair;
import thesis.antlersolver.reductions.AntlerReduction;
import thesis.antlersolver.solvers.AntlerReductionSolver;
import thesis.antlersolver.solvers.TimedSolve;
import thesis.antlersolver.statistics.Statistics;

public class AntlerSolver {

    public static final long maxTime = 30*60*1000;
    public static final long maxKernelStepTime = 5*1000;
    public static final int PathAntlerKernel = 20;
    public static final int PathAntlerKernelHeuristic = 40;
    public static final int PathAntlerKernelHeuristicSize = 1000;
    public static final int AntlerKernel = 4;
    public static final int AntlerKernelHeuristic = 20;
    public static final int SingleTreeAntlerKernel = 10;

    public static void main(String[] args) {

        if (args.length != 3 || (!args[2].equals("solve") && !args[2].equals("kernel") && !args[2].equals("solve_and_kernel"))) {
			printHelp();
			return;
		}

        // initiallize one of the lambda expressions
        for(int i = 0; i < AntlerReduction.HeuristicType.values().length; i++) {
        final int t = i;
        antlerHeuristicReduce[i] = (Graph reductionGraph, int reductionK) -> {
            for(int j = 1; j <= reductionK; j++) {
                if(j == 1) {
                    AntlerReduction.singleAntler(reductionGraph);
                    continue;
                }
                if(AntlerReduction.kAntlerHeuristic(reductionGraph, j, false, true, 10,
                        AntlerReduction.HeuristicType.values()[t])) {
                    j = 0;
                    continue;
                }
                if(AntlerReduction.kAntlerHeuristic(reductionGraph, j, false, false, 10,
                    AntlerReduction.HeuristicType.values()[t])) j = 0;
            }
        };
    }
        
        ReductionRoot.DEBUG = false;
        //Debug.silent = true;
        Pair<Graph[], String[]> graphPairs = new Pair<Graph[], String[]>(new Graph[1], new String[1]);
        if(args[0].length() >= 8 && args[0].substring(0, 8).equals("!random=")) {
            String folder = args[0].substring(8);
            Random rand = new Random();
            int g = 0;
            Graph[] graphs = new Graph[110];
            String[] names = new String[110];
            for(int t = 0; t < 2; t++) {
                for(int i = 50; i <= 100; i+=5) {
                    int k = rand.nextInt(AntlerKernel-1-1)+2;
                    int a = rand.nextInt(k/2+1)+k/2;
                    int m = rand.nextInt(10)+1;
                    System.out.print("                  Making Antler graph "+((i-50)/5)+" with k: "+k+", a: "+a+", and m: "+m+"                                                            \r");
                    graphs[g] = GraphAlgorithm.randomAntlerGraph(i, k, 110-i, m, 0.75, a, 0.2, 0.5);
                    if(graphs[g].n != (i+k)*m+(110-i)) System.out.println("ERROR WITH GENERATION ANTLER");
                    names[g++] = "Antler_F="+i+"_K="+k+"_R="+(110-i)+"_A="+a+"_M="+m+"_V"+(t+1);
                    System.out.print("Made Antler graph "+((i-50)/5)+"                                                                                                                        \r");
                }
                for(int i = 50; i <= 100; i+=5) {
                    int k = rand.nextInt(AntlerKernelHeuristic-1-10)+2;
                    int a = rand.nextInt(k/2+1)+k/2;
                    int m = rand.nextInt(10)+1;
                    System.out.print("                  Making Heuristic Antler graph "+((i-50)/5)+" with k: "+k+", a: "+a+", and m: "+m+"                                                            \r");
                    graphs[g] = GraphAlgorithm.randomAntlerGraph(i, k, 110-i, m, 0.75, a, 0.2, 0.5);
                    if(graphs[g].n != (i+k)*m+(110-i)) System.out.println("ERROR WITH GENERATION HEURISTIC ANTLER");
                    names[g++] = "AntlerH_F="+i+"_K="+k+"_R="+(110-i)+"_A="+a+"_M="+m+"_V"+(t+1);
                    System.out.print("Made Heuristic Antler graph "+((i-50)/5)+"                                                                                                                        \r");
                }
                for(int i = 50; i <= 100; i+=5) {
                    int k = rand.nextInt(PathAntlerKernel-1-10)+2;
                    int a = rand.nextInt(k/2+1)+k/2;
                    int m = rand.nextInt(10)+1;
                    System.out.print("                  Making Path Antler graph "+((i-50)/5)+" with k: "+k+", a: "+a+", and m: "+m+"                                                            \r");
                    graphs[g] = GraphAlgorithm.randomPathAntlerGraph(i, k, 110-i, m, a, 0.2, 0.5);
                    if(graphs[g].n != (i+k)*m+(110-i)) System.out.println("ERROR WITH GENERATION PATH ANTLER");
                    names[g++] = "PathAntler_F="+i+"_K="+k+"_R="+(110-i)+"_A="+a+"_M="+m+"_V"+(t+1);
                    System.out.print("Made Path Antler graph "+((i-50)/5)+"                                                                                                                        \r");
                }
                for(int i = 50; i <= 100; i+=5) {
                    int k = rand.nextInt(PathAntlerKernelHeuristic-1-20)+2;
                    int a = rand.nextInt(k/2+1)+k/2;
                    int m = rand.nextInt(10)+1;
                    System.out.print("                  Making Heuristic Path Antler graph "+((i-50)/5)+" with k: "+k+", a: "+a+", and m: "+m+"                                                            \r");
                    graphs[g] = GraphAlgorithm.randomPathAntlerGraph(i, k, 110-i, m, a, 0.2, 0.5);
                    if(graphs[g].n != (i+k)*m+(110-i)) System.out.println("ERROR WITH GENERATION HEURISTIC PATH ANTLER");
                    names[g++] = "PathAntlerH_F="+i+"_K="+k+"_R="+(110-i)+"_A="+a+"_M="+m+"_V"+(t+1);
                    System.out.print("Made Heuristic Path Antler graph "+((i-50)/5)+"                                                                                                                        \r");
                }
                for(int i = 50; i <= 100; i+=5) {
                    int k = rand.nextInt(SingleTreeAntlerKernel-1-3)+2;
                    int a = rand.nextInt(k/2+1)+k/2;
                    int m = rand.nextInt(10)+1;
                    System.out.print("                  Making Single Tree Antler graph "+((i-50)/5)+" with k: "+k+", a: "+a+", and m: "+m+"                                                            \r");
                    graphs[g] = GraphAlgorithm.randomSingleTreeAntlerGraph(i, k, 110-i, m, a, 0.2, 0.5);
                    if(graphs[g].n != (i+k)*m+(110-i)) System.out.println("ERROR WITH GENERATION SINGLE TREE ANTLER");
                    names[g++] = "SingleTreeAntler_F="+i+"_K="+k+"_R="+(110-i)+"_A="+a+"_M="+m+"_V"+(t+1);
                    System.out.print("Made Single Tree Antler graph "+((i-50)/5)+"                                                                                                                        \r");
                }
            }
            graphPairs = new Pair<Graph[],String[]>(graphs, names);
            for(int i = 0; i < graphs.length; i++) {
                try {
                    FileWriter fw = new FileWriter(new File(folder+"/"+names[i]+".graph"));
                    fw.write(graphs[i].toString());
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                File input = new File(args[0]);
                if(input.isDirectory()) {
                    graphPairs = FileReader.readGraphDir(args[0]);
                } else {
                    Pair<Graph, String> graph = FileReader.readGraph(args[0]);
                    graphPairs.a[0] = graph.a;
                    graphPairs.b[0] = graph.b;
                }
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }
        }
        Graph[] graphs = graphPairs.a;
        String[] names = graphPairs.b;
        Integer[] sortIndices = new Integer[graphs.length];
        for(int i = 0; i < graphs.length; i++) sortIndices[i] = i;
        Arrays.sort(sortIndices, (i1, i2) -> 
            graphs[i1].n() == graphs[i2].n() ? graphs[i1].m()-graphs[i2].m() : graphs[i1].n()-graphs[i2].n());
        //Arrays.sort(sortIndices, (i1, i2) -> Integer.parseInt(names[i1])-Integer.parseInt(names[i2]));

        if(args[2].equals("kernel") || args[2].equals("solve_and_kernel")) {
            kernel(graphs, names, sortIndices, args[1]);
        }
        if(args[2].equals("solve") || args[2].equals("solve_and_kernel")) {
            solve(graphs, names, sortIndices, args[1]);
        }
    }

    public static void solve(Graph[] graphs, String[] names, Integer[] order, String out) {
        ReductionRoot.DEBUG = false;
        Debug.silent = true;
        int counter = 0;
        int kernelCounter = 0;
        long cummulativeTime = 0;
        long totalTimer = -System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("graph,n,m,n\',m\',s\',kernelTime,fvs,time\n");
        for(int gCount = 0; gCount < graphs.length; gCount++) {
            int g = order[gCount];
            Graph graph = graphs[g];
            int originalsizeN = graph.n();
            int originalsizeM = graph.m();
            System.out.println("Start "+names[g]+" with "+originalsizeN+" nodes and "+originalsizeM+" edges");
            long time = -System.currentTimeMillis();
            //ReductionRoot.reduce(graph);
            allKernelApply(graph);
            System.out.println("graph "+names[g]+" reduction: ("+originalsizeN+", "+originalsizeM+") -> ("+graph.n()+", "+graph.m()+", "+graph.k+") in "+(time+System.currentTimeMillis())+" ms");
            sb.append(names[g]+","+originalsizeN+","+originalsizeM+","+graph.n()+","+graph.m()+","+graph.k+","+(time+System.currentTimeMillis())+",");
            Statistics.getStat().print(out+"/"+names[g]+"Reduction.txt");
            Statistics.getStat().print();
            Statistics.reset();
            if(maxTime >= time+System.currentTimeMillis() && graph.n() == 0) kernelCounter++; 
            TimedSolve solver = new TimedSolve(new AntlerReductionSolver());
            int[] fvs = solver.solve(graph, maxTime-(time+System.currentTimeMillis()));
            if(fvs == null) {
                System.out.println("graph "+names[g]+" failed to compute");
                System.out.println(kernelCounter+"/"+counter+"/"+(gCount+1)+"/"+graphs.length);
                Statistics.getStat().print(out+"/"+names[g]+"Branching.txt");
                Statistics.getStat().print();
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
            System.out.println(kernelCounter+"/"+counter+"/"+(gCount+1)+"/"+graphs.length);
            Statistics.getStat().print(out+"/"+names[g]+"Branching.txt");
            Statistics.getStat().print();
            Statistics.reset();
            sb.append(fvs.length+","+time+"\n");
            // try {
            //     FileWriter fw = new FileWriter(new File(out+"/"+names[g]+"Sol.txt"));
            //     for(int v : fvs) fw.write(v+" ");
            //     fw.close();
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }
        }
        totalTimer += System.currentTimeMillis();
        System.out.println(counter+" graphs solved in "+cummulativeTime+" ms");
        System.out.println("Total running time of "+totalTimer+" ms");
        try {
            FileWriter fw = new FileWriter(new File(out+"/statistics.csv"));
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // reduction lambda expressions
    static Reduction pathAntlerReduce = (Graph reductionGraph, int reductionK) -> {
        int last = reductionGraph.n();
        do {
            AntlerReduction.singleAntler(reductionGraph);
            last = reductionGraph.n();
            AntlerReduction.kPathAntlers(reductionGraph, reductionK, false, Integer.MAX_VALUE);
        } while(reductionGraph.n() != last);
    };
    static Reduction pathAntlerHeuristicReduce = (Graph reductionGraph, int reductionK) -> {
        int last = reductionGraph.n();
        do {
            AntlerReduction.singleAntler(reductionGraph);
            last = reductionGraph.n();
            AntlerReduction.kPathAntlers(reductionGraph, reductionK, false, PathAntlerKernelHeuristic);
        } while(reductionGraph.n() != last);
    };
    static Reduction antlerReduce = (Graph reductionGraph, int reductionK) -> {
        for(int j = 1; j <= reductionK; j++) {
            if(j == 1) {
                AntlerReduction.singleAntler(reductionGraph);
                continue;
            }
            if(AntlerReduction.kAntler(reductionGraph, j, false)) j = 0;
        }
    };
    static Reduction[] antlerHeuristicReduce = new Reduction[AntlerReduction.HeuristicType.values().length];
    static Reduction allAntlerHeuristicReduce = (Graph reductionGraph, int reductionK) -> {
        for(int j = 1; j <= reductionK; j++) {
            if(j == 1) {
                AntlerReduction.singleAntler(reductionGraph);
                continue;
            }
            for(AntlerReduction.HeuristicType type : AntlerReduction.HeuristicType.values()) {
                if(AntlerReduction.kAntlerHeuristic(reductionGraph, j, false, true, 10, type)) {
                    j = 0;
                    break;
                }
                if(AntlerReduction.kAntlerHeuristic(reductionGraph, j, false, false, 10, type)) {
                    j = 0;
                    break;
                }
            }
        }
    };
    static Reduction singleTreeAntlerReduce = (Graph reductionGraph, int reductionK) -> {
        int last = reductionGraph.n();
        do {
            AntlerReduction.singleAntler(reductionGraph);
            last = reductionGraph.n();
            AntlerReduction.singleTreeAntler(reductionGraph, reductionK);
        } while(reductionGraph.n() != last);
    };

    public static void allKernelApply(Graph graph) {
        for(;;) {
            System.out.print("Kernel Stats: "+graph.n()+", "+graph.m()+", "+graph.k+"                                    \r");
            if(AntlerReduction.singleAntler(graph)) continue;
            ReductionState state = timedReduction(graph, PathAntlerKernelHeuristic, maxKernelStepTime, pathAntlerHeuristicReduce);
            if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
            state = timedReduction(graph, AntlerKernelHeuristic, maxKernelStepTime, allAntlerHeuristicReduce);
            if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
            state = timedReduction(graph, PathAntlerKernel, maxKernelStepTime, pathAntlerReduce);
            if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
            state = timedReduction(graph, SingleTreeAntlerKernel, maxKernelStepTime, singleTreeAntlerReduce);
            if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
            state = timedReduction(graph, AntlerKernel, maxKernelStepTime, antlerReduce);
            if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
            int oldn = graph.n(), oldm = graph.m();
            ReductionRoot.reduce(graph);
            if(oldn != graph.n() || oldm != graph.m()) continue;
            break;
        }
    }

    public static void kernel(Graph[] graphs, String[] names, Integer[] order, String out) {
        long totalTimer = -System.currentTimeMillis();

        // start of csv
        StringBuilder sb = new StringBuilder();
        sb.append("graph,n,m,s,");
        for(int i = 1; i <= PathAntlerKernel; i++) {
            sb.append("p"+i+"n,p"+i+"m,p"+i+"s,");
        }
        for(int i = 1; i <= PathAntlerKernelHeuristic; i++) {
            sb.append("ph"+i+"n,ph"+i+"m,ph"+i+"s,");
        }
        for(int i = 1; i <= AntlerKernel; i++) {
            sb.append("a"+i+"n,a"+i+"m,a"+i+"s,");
        }
        for(AntlerReduction.HeuristicType type : AntlerReduction.HeuristicType.values()) {
            for(int i = 1; i <= AntlerKernelHeuristic; i++) {
                sb.append("ah"+type+i+"n,ah"+type+i+"m,ah"+type+i+"s,");
            }
        }
        for(int i = 1; i <= SingleTreeAntlerKernel; i++) {
            sb.append("t"+i+"n,t"+i+"m,t"+i+"s,");
        }
        sb.append("alln,allm,alls,kerneln,kernelm,kernels,combin,combim,combis\n");

        // counters
        int allbest = 0;
        int draw = 0;
        int kernelbest = 0;
        int combibest = 0;
        int solved = 0;

        for(int gCount = 0; gCount < graphs.length; gCount++) {
            long graphTime = System.currentTimeMillis();
            int g = order[gCount];
            Graph graph = graphs[g];
            System.out.println("Start: "+names[g]+": "+graph.n()+", "+graph.m());
            sb.append(names[g]+","+graph.n()+","+graph.m()+",0,");
            for(int i = 1; i <= PathAntlerKernel; i++) {
                Graph testGraph = new Graph(graph);
                final int k = i;
                ReductionState state = timedReduction(testGraph, k, maxKernelStepTime, pathAntlerReduce);
                if(state == ReductionState.TimeoutReduced || state == ReductionState.TimeoutNonReduced) {
                    for(int j = i; j <= PathAntlerKernel; j++) {
                        sb.append(",,,");
                    }
                    break;
                }
                sb.append(testGraph.n()+","+testGraph.m()+","+testGraph.k+",");
                System.out.print("P"+i+" done                         \r");
            }
            for(int i = 1; i <= PathAntlerKernelHeuristic; i++) {
                Graph testGraph = new Graph(graph);
                final int k = i;
                ReductionState state = timedReduction(testGraph, k, maxKernelStepTime, pathAntlerHeuristicReduce);
                if(state == ReductionState.TimeoutReduced || state == ReductionState.TimeoutNonReduced) {
                    for(int j = i; j <= PathAntlerKernelHeuristic; j++) {
                        sb.append(",,,");
                    }
                    break;
                }
                sb.append(testGraph.n()+","+testGraph.m()+","+testGraph.k+",");
                System.out.print("PH"+i+" done                         \r");
            }
            for(int i = 1; i <= AntlerKernel; i++) {
                Graph testGraph = new Graph(graph);
                final int k = i;
                ReductionState state = timedReduction(testGraph, k, maxKernelStepTime, antlerReduce);
                if(state == ReductionState.TimeoutReduced || state == ReductionState.TimeoutNonReduced) {
                    for(int j = i; j <= AntlerKernel; j++) {
                        sb.append(",,,");
                    }
                    break;
                }
                sb.append(testGraph.n()+","+testGraph.m()+","+testGraph.k+",");
                System.out.print("A"+i+" done                         \r");
            }
            for(int t = 0; t < AntlerReduction.HeuristicType.values().length; t++) {
                AntlerReduction.HeuristicType type = AntlerReduction.HeuristicType.values()[t];
                for(int i = 1; i <= AntlerKernelHeuristic; i++) {
                    Graph testGraph = new Graph(graph);
                    final int k = i;
                    ReductionState state = timedReduction(testGraph, k, maxKernelStepTime, antlerHeuristicReduce[t]);
                    if(state == ReductionState.TimeoutReduced || state == ReductionState.TimeoutNonReduced) {
                        for(int j = i; j <= AntlerKernelHeuristic; j++) {
                            sb.append(",,,");
                        }
                        break;
                    }
                    sb.append(testGraph.n()+","+testGraph.m()+","+testGraph.k+",");
                    System.out.print("AH"+type+i+" done                         \r");
                }
            }
            for(int i = 1; i <= SingleTreeAntlerKernel; i++) {
                Graph testGraph = new Graph(graph);
                final int k = i;
                ReductionState state = timedReduction(testGraph, k, maxKernelStepTime, singleTreeAntlerReduce);
                if(state == ReductionState.TimeoutReduced || state == ReductionState.TimeoutNonReduced) {
                    for(int j = i; j <= SingleTreeAntlerKernel; j++) {
                        sb.append(",,,");
                    }
                    break;
                }
                sb.append(testGraph.n()+","+testGraph.m()+","+testGraph.k+",");
                System.out.print("T"+i+" done                         \r");
            }
            long allTime = -System.currentTimeMillis();
            Graph testGraph = new Graph(graph);
            for(;;) {
                System.out.print("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k+" | Single Antler                                    \r");
                if(AntlerReduction.singleAntler(testGraph)) continue;
                System.out.print("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k+" | H Path Antler                                    \r");
                ReductionState state = timedReduction(testGraph, PathAntlerKernelHeuristic, maxKernelStepTime, pathAntlerHeuristicReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k+" | H Antler                                    \r");
                state = timedReduction(testGraph, AntlerKernelHeuristic, maxKernelStepTime, allAntlerHeuristicReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k+" | Path Antler                                    \r");
                state = timedReduction(testGraph, PathAntlerKernel, maxKernelStepTime, pathAntlerReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k+" | Single Tree Antler                                    \r");
                state = timedReduction(testGraph, SingleTreeAntlerKernel, maxKernelStepTime, singleTreeAntlerReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k+" | Antler                                    \r");
                state = timedReduction(testGraph, AntlerKernel, maxKernelStepTime, antlerReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                break;
            }
            allTime += System.currentTimeMillis();

            sb.append(testGraph.n()+","+testGraph.m()+","+testGraph.k+",");
            System.out.print("All done                                                      \r");
            System.out.println("All: "+testGraph.n()+", "+testGraph.m()+", "+testGraph.k);
            Graph kernelGraph = new Graph(graph);
            ReductionRoot.reduce(kernelGraph);
            sb.append(kernelGraph.n()+","+kernelGraph.m()+","+kernelGraph.k+",");
            System.out.print("Kernel done                                                      \r");
            System.out.println("Kernel: "+kernelGraph.n()+", "+kernelGraph.m()+", "+kernelGraph.k);
            Graph combiGraph = new Graph(testGraph);

            long combiTime = -System.currentTimeMillis();
            int oldnstart = combiGraph.n(), oldmstart = combiGraph.m();
            ReductionRoot.reduce(combiGraph);
            boolean goLoop = oldnstart != combiGraph.n() || oldmstart != combiGraph.m();
            for(;goLoop;) {
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | Single Antler                                    \r");
                if(AntlerReduction.singleAntler(combiGraph)) continue;
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | H Path Antler                                    \r");
                ReductionState state = timedReduction(combiGraph, PathAntlerKernelHeuristic, maxKernelStepTime, pathAntlerHeuristicReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | H Antler                                    \r");
                state = timedReduction(combiGraph, AntlerKernelHeuristic, maxKernelStepTime, allAntlerHeuristicReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | Path Antler                                    \r");
                state = timedReduction(combiGraph, PathAntlerKernel, maxKernelStepTime, pathAntlerReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | Single Tree Antler                                    \r");
                state = timedReduction(combiGraph, SingleTreeAntlerKernel, maxKernelStepTime, singleTreeAntlerReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | Antler                                    \r");
                state = timedReduction(combiGraph, AntlerKernel, maxKernelStepTime, antlerReduce);
                if(state == ReductionState.Reduced || state == ReductionState.TimeoutReduced) continue;
                int oldn = combiGraph.n(), oldm = combiGraph.m();
                System.out.print("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k+" | Kernel                                    \r");
                ReductionRoot.reduce(combiGraph);
                if(oldn != combiGraph.n() || oldm != combiGraph.m()) continue;
                break;
            }
            combiTime += System.currentTimeMillis()+allTime;
            sb.append(combiGraph.n()+","+combiGraph.m()+","+combiGraph.k+"\n");
            System.out.print("Combi done                                                                                          \r");
            System.out.println("Combi: "+combiGraph.n()+", "+combiGraph.m()+", "+combiGraph.k);
            if(testGraph.k > kernelGraph.k) allbest++;
            if(testGraph.k < kernelGraph.k) kernelbest++;
            if(testGraph.k == kernelGraph.k) draw++;
            if(combiGraph.k > kernelGraph.k && combiGraph.k > testGraph.k) combibest++;
            if(combiGraph.n() == 0) solved++;
            System.out.println("Graph "+names[g]+" checked in "+(System.currentTimeMillis()-graphTime)+" ms of which all took "+allTime+" ms and combi took "+combiTime+" ms");
            long expectedTimeLeft = Math.round(((graphs.length-gCount+1.0)/graphs.length)*((System.currentTimeMillis()+totalTimer)/((gCount+1.0)/graphs.length)));
            System.out.println("Time spend: "+(System.currentTimeMillis()+totalTimer)+" ms, Expected time left: "+expectedTimeLeft+" ms, Percentage done: "+(100*((gCount+1.0)/graphs.length)));
            System.out.println((gCount+1)+"/"+graphs.length+"     "+allbest+" - "+draw+" - "+kernelbest+" - "+combibest+" - "+solved);
        }
        totalTimer += System.currentTimeMillis();
        System.out.println("Total running time of "+totalTimer+" ms");
        try {
            FileWriter fw = new FileWriter(new File(out+"/kernelstatistics.csv"));
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface Reduction {
        void reduce(Graph graph, int k);
    }

    public enum ReductionState {
        Reduced,
        NonReduced,
        TimeoutReduced,
        TimeoutNonReduced,
    }

    public static ReductionState timedReduction(Graph graph, int k, long time, Reduction reduction) {
        int last = graph.n();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> reduction.reduce(graph, k));
        try {
            future.get(time, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            executor.shutdownNow();
            if(last != graph.n()) return ReductionState.TimeoutReduced;
            return ReductionState.TimeoutNonReduced;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
        if(last != graph.n()) return ReductionState.Reduced;
        return ReductionState.NonReduced;
    }

    private static void printHelp() {
		System.out.println("AntlerSolver - an application that implements a new algorithm for\n" +
				"solving the feedback vertex set problem on multigraphs.\n" +
				"\n" +
				"Usage: java -jar Antlersolver.jar <graphFile/graphDir> <outputDir> <solve/kernel>\n" +
				"\n" +
				"graphFile/graphDir can be any .edges file or directory containing .edges files.\n" +
				"Each .edges file consists of lines which are either comments (starting with # or %),\n" +
				"or 2 space seperated numbers x y representing an edge between node x and node y.\n" +
                "This field can be replaced with `!random=graphDir` for the algorithm to create random\n " +
                "graphs that contain specific antler structures and make graph files in graphDir.\n" +
				"\n" +
				"outputDir is the directory where files for the minimum FVS per .edges file will be stored.\n" + 
                "\n" +
                "solve/kernel/solve_and_kernel is the option to choose wether to use the solver or kernalization or both on the graphs.");
	}
}
