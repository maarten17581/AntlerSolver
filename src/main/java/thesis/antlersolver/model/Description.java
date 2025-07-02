package thesis.antlersolver.model;

import java.util.Arrays;

import fvs_wata_orz.Graph;

public class Description {
    public int r;
    public int[][] X;

    public Description(int r, int[][] X) {
        this.r = r;
        this.X = X;
    }

    public void addX(int[] x) {
        int[][] a = new int[X.length + 1][];
        boolean added = false;
        loop : for(int i = 0; i < X.length; i++) {
            if(X[i].length < x.length) continue;
            if(X[i].length == x.length) {
                for(int j = 0; j < x.length; j++) {
                    if(X[i][j] < x[j]) continue loop;
                    if(X[i][j] > x[j]) break;
                }
            }
            System.arraycopy(X, 0, a, 0, i);
            a[i] = x;
            System.arraycopy(X, i, a, i+1, X.length-i);
            added = true;
            break;
        }
        if(!added) {
            System.arraycopy(X, 0, a, 0, X.length);
            a[X.length] = x;
        }
        X = a;
    }

    public void setSmallR(Graph g) {
        boolean[] visited = new boolean[g.n];
        for(int[] x : X) for(int v : x) visited[v] = true;
        visited[r] = true;
        int[] queue = new int[g.n];
        queue[0] = r;
        int queueSize = 1;
        int index = 0;
        while(index < queueSize) {
            int node = queue[index++];
            for(int nb : g.adj[node]) {
                if(visited[nb]) continue;
                visited[nb] = true;
                queue[queueSize++] = nb;
                r = Math.min(r, nb);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Description)) return false;
        Description otherDesc = (Description)other;
        if(otherDesc.r != r) return false;
        if(otherDesc.X.length != X.length) return false;
        for(int i = 0; i < X.length; i++) if(otherDesc.X[i].length != X[i].length) return false;
        for(int i = 0; i < X.length; i++) for(int j = 0; j < X[i].length; j++) if(otherDesc.X[i][j] != X[i][j]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = r;
        for(int[] x : X) for(int v : x) result = prime * result + v;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(r).append(", ").append("[");
        for (int i = 0; i < X.length; i++) {
            sb.append(Arrays.toString(X[i]));
            if (i < X.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("])");
        return sb.toString();
    }
}