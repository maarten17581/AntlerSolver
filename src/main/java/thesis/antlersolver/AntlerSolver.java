package thesis.antlersolver;

import java.io.File;
import java.io.IOException;

import thesis.antlersolver.io.FileReader;
import thesis.antlersolver.model.Graph;

public class AntlerSolver {
    public static void main(String[] args) {
        if (args.length != 2) {
			printHelp();
			return;
		}

        try {
            File input = new File(args[0]);
            Graph[] graphs = new Graph[1];
            if(input.isDirectory()) {
                graphs = FileReader.readGraphDir(args[0]);
            } else {
                graphs[0] = FileReader.readGraph(args[0]);
            }
            for(Graph graph : graphs) {
                System.out.println(graph);
            }
        } catch(IOException e) {
            e.printStackTrace();
            return;
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
