package thesis.antlersolver.algorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTest {

    @Test
    public void testCompute() {
        Algorithm algo = new Algorithm();
        double input = 4.0;
        double expected = Math.sqrt(16 + 10);  // (4^2 + 10)
        double actual = algo.compute(input);

        assertEquals(expected, actual, 0.0001);
    }
}