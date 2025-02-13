package thesis.antlersolver.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import thesis.antlersolver.model.Graph;

public class FileReader {
    public static Graph readGraph(String filePath) throws IOException {
        File file = new File(filePath);

        Scanner sc = new Scanner(file);

        ArrayList<Integer> edgeS = new ArrayList<>();
        ArrayList<Integer> edgeT = new ArrayList<>();
        Set<Integer> nodes = new HashSet<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if(line.charAt(0) == '#' || line.charAt(0) == '%') {
                continue;
            }
            Scanner scline = new Scanner(line);
            int x = scline.nextInt();
            int y = scline.nextInt();
            edgeS.add(x);
            edgeT.add(y);
            nodes.add(x);
            nodes.add(y);
            scline.close();
        }
        sc.close();

        Graph graph = new Graph(file.getName());
        for(Integer i : nodes) {
            graph.addNode(i);
        }
        for(int i = 0; i < edgeS.size(); i++) {
            graph.addEdge(edgeS.get(i), edgeT.get(i));
        }
        return graph;
    }

    public static Graph[] readGraphDir(String dirPath) throws IOException {
        ArrayList<Graph> graphs = new ArrayList<>();
        File dir = new File(dirPath);
        if(!dir.isDirectory()) {
            throw new IOException("File "+dirPath+" is not a directory");
        }
        File[] directoryListing = dir.listFiles();
        for (File child : directoryListing) {
            if(child.isDirectory()) {
                graphs.addAll(Arrays.asList(readGraphDir(child.getAbsolutePath())));
            } else {
                graphs.add(readGraph(child.getAbsolutePath()));
            }
        }
        return graphs.toArray(new Graph[0]);
    }
}
