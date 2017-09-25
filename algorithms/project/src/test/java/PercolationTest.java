import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.StdOut;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import org.apache.commons.io.IOUtils;


public class PercolationTest {
    @Test
    void n1NoOpenSites() {
        Percolation p = new Percolation(1);
        assertFalse(p.percolates());
    }

    @Test
    void n1OpenSites() {
        Percolation p = new Percolation(1);
        p.open(1, 1);
        assertTrue(p.percolates());
    }

    @Test
    void constructorExceptionsNegativeSize() {
        try {
            Percolation p = new Percolation(-1);
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
            Percolation p = new Percolation(0);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // to nothing
        } catch (Exception e) {
            fail(String.format("Should throw IllegalArgumentException. Got: %s", e.getClass()));
        }
    }

    @Test
    void checkFullOnInput10() throws IOException {
        TestData td = new TestData("/input10.txt");
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(9, 1));
    }

    @Test
    void checkFullOnInput20() throws IOException {
        TestData td = new TestData("/input20.txt");
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(18, 1));
    }

    @Test
    void checkFullOnInput50() throws IOException {
        TestData td = new TestData("/input50.txt");
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(22, 28));
    }

    @Test
    void checkFullOnJerry47() throws IOException {
        TestData td = new TestData("/jerry47.txt", 1076);
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(11, 47));
    }

    @Test
    void checkFullWayne98() throws IOException {
        TestData td = new TestData("/wayne98.txt", 3851);
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(69, 9));
    }

    @Test
    void checkFullInput3() throws IOException {
        TestData td = new TestData("/input3.txt", 4);
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(3, 1));
    }

    @Test
    void checkFullInput4() throws IOException {
        TestData td = new TestData("/input4.txt", 7);
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(4, 4));
    }

    @Test
    void checkFullInput7() throws IOException {
        TestData td = new TestData("/input7.txt", 12);
        Percolation p = new Percolation(td.size);
        for (Coordinate coord: td.coords) {
            p.open(coord.row, coord.col);
        }

        assertFalse(p.isFull(6, 1));
    }

    private static class Coordinate {
        int row;
        int col;

        Coordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
    private static class TestData {
        int size;
        List<Coordinate> coords = new ArrayList<Coordinate>();

        TestData(String fileName) throws IOException {
            this(fileName, Integer.MAX_VALUE);
        }

        TestData(String fileName, int maxEntries) throws IOException {
            List<String> lines = IOUtils.readLines(this.getClass().getResourceAsStream(fileName), "UTF-8");

            size = Integer.parseInt(lines.get(0));
            lines.remove(0);

            List<String> cLines = lines.subList(0, Math.min(maxEntries, lines.size()));



            for (String line : cLines) {
                String[] tokens = line.trim().split("\\s+");
                int row = Integer.parseInt(tokens[0].trim());
                int col = Integer.parseInt(tokens[1].trim());
                coords.add(new Coordinate(row, col));
            }

//            StdOut.printf("Coordinate count: %d%n", coords.size());
        }
    }
}
