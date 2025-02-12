package thesis.antlersolver.algorithm;

import thesis.antlersolver.utils.MathUtils;
import thesis.antlersolver.models.Result;

public class Algorithm {

    public double compute(double input) {
        // Example: applying a simple mathematical function for demonstration
        double intermediate = MathUtils.square(input);
        return MathUtils.sqrt(intermediate + 10);
    }
}
