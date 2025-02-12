package thesis.antlersolver;

import thesis.antlersolver.algorithm.Algorithm;

public class Main {
    public static void main(String[] args) {
        Algorithm algo = new Algorithm();
        double input = 42.0;
        double result = algo.compute(input);

        System.out.println("Result of the computation: " + result);
    }
}
