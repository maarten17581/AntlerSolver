package thesis.antlersolver.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fvs_wata_orz.Graph;
import fvs_wata_orz.GraphIO;
import thesis.antlersolver.model.Pair;

public class FileReader {
    public static Pair<Graph, String> readGraph(String filePath) throws IOException {
        File file = new File(filePath);
        GraphIO io = new GraphIO();
        io.read(new FileInputStream(file));
        String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
        return new Pair<Graph,String>(new Graph(io.adj), name);
    }

    public static Pair<Graph[], String[]> readGraphDir(String dirPath) throws IOException {
        List<Graph> graphs = new ArrayList<>();
        List<String> names = new ArrayList<>();
        File dir = new File(dirPath);
        if(!dir.isDirectory()) {
            throw new IOException("File "+dirPath+" is not a directory");
        }
        File[] directoryListing = dir.listFiles();
        for (File child : directoryListing) {
            if(child.isDirectory()) {
                Pair<Graph[], String[]> childGraphs = readGraphDir(child.getAbsolutePath());
                graphs.addAll(Arrays.asList(childGraphs.a));
                names.addAll(Arrays.asList(childGraphs.b));
            } else {
                Pair<Graph, String> graph = readGraph(child.getAbsolutePath());
                graphs.add(graph.a);
                names.add(graph.b);
            }
        }
        return new Pair<Graph[], String[]>(graphs.toArray(new Graph[0]), names.toArray(new String[0]));
    }
}
