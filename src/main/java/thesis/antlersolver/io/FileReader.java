package thesis.antlersolver.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import thesis.antlersolver.model.Graph;

public class FileReader {
    public static Graph readGraph(String filePath) throws IOException {
        File file = new File(filePath);

        Scanner sc = new Scanner(file);

        List<Integer> edgeS = new ArrayList<>();
        List<Integer> edgeT = new ArrayList<>();
        Set<Integer> nodes = new HashSet<>();

        while (sc.hasNextLine()) {
            char[] line = sc.nextLine().toCharArray();
            if(line[0] == '#' || line[0] == '%') {
                continue;
            }
            int x = 0;
            int y = 0;
            boolean second = false;
            for(int i = 0; i < line.length; i++) {
                if(line[i] == ' ') {
                    second = true;
                    continue;
                }
                if(!second) {
                    x *= 10;
                    x += line[i]-'0';
                } else {
                    y *= 10;
                    y += line[i]-'0';
                }
            }
            edgeS.add(x);
            edgeT.add(y);
            nodes.add(x);
            nodes.add(y);
        }
        sc.close();

        Graph graph = new Graph(file.getName().substring(0, file.getName().indexOf('.')));
        for(Integer i : nodes) {
            graph.addNode(i);
        }
        for(int i = 0; i < edgeS.size(); i++) {
            graph.addEdge(edgeS.get(i), edgeT.get(i));
        }
        return graph;
    }

    public static Graph[] readGraphDir(String dirPath) throws IOException {
        List<Graph> graphs = new ArrayList<>();
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
