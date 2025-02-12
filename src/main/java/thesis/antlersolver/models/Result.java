package thesis.antlersolver.models;

public class Result {
    private final double value;

    public Result(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Result{value=" + value + '}';
    }
}
