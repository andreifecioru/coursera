import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class PercolationStatsTest {
    @Test
    void constructorExceptionsNegativeSize() {
        try {
            PercolationStats ps = new PercolationStats(-23, 42);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // to nothing
        } catch (Exception e) {
            fail(String.format("Should throw IllegalArgumentException. Got: %s", e.getClass()));
        }
    }

    @Test
    void constructorExceptionsNegativeTrials() {
        try {
            PercolationStats ps = new PercolationStats(23, -42);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // to nothing
        } catch (Exception e) {
            fail(String.format("Should throw IllegalArgumentException. Got: %s", e.getClass()));
        }
    }
    @Test
    void constructorExceptionsZeroSize() {
        try {
            PercolationStats ps = new PercolationStats(0, 1);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // to nothing
        } catch (Exception e) {
            fail(String.format("Should throw IllegalArgumentException. Got: %s", e.getClass()));
        }
    }

    @Test
    void constructorExceptionsZeroTrials() {
        try {
            PercolationStats ps = new PercolationStats(1, 0);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // to nothing
        } catch (Exception e) {
            fail(String.format("Should throw IllegalArgumentException. Got: %s", e.getClass()));
        }
    }
}
