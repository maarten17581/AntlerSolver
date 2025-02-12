package thesis.antlersolver.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MathUtilsTest {

    @Test
    public void testSquare() {
        assertEquals(25, MathUtils.square(5));
    }

    @Test
    public void testSqrt() {
        assertEquals(3, MathUtils.sqrt(9), 0.0001);
    }
}