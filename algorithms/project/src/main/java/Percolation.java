import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    private final boolean[][] grid;
    private final int n;
    private final int size;
    private final WeightedQuickUnionUF ufPercolate;
    private final WeightedQuickUnionUF ufFull;
    private int openSitesCount = 0;

    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive integer.");

        size = n * n;
        this.n = n;

        grid = new boolean[n][n];
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                grid[r][c] = false;

        // also create 2 extra virtual nodes
        ufPercolate = new WeightedQuickUnionUF(size + 2);
        ufFull = new WeightedQuickUnionUF(size + 1);
        for (int c = 0; c < n; c++) {
            // connect the 1st virtual node to top row
            ufPercolate.union(c, size);
            ufFull.union(c, size);

            // connect the 2nd virtual node to top row
            ufPercolate.union((n - 1) * n + c, size + 1);
        }
    }

    private void checkRowAndCol(int row, int col) {
        if ((row < 1) || (row > n))
            throw new IllegalArgumentException(String.format("illegal value for row: %d (n: %d)", row, n));
        if ((col < 1) || (col > n))
            throw new IllegalArgumentException(String.format("illegal value for col: %d (n: %d)", col, n));
    }

    private void _union(int p, int q) {
        ufPercolate.union(p, q);
        ufFull.union(p, q);
    }

    public void open(int row, int col) {
        checkRowAndCol(row, col);

        if (isOpen(row, col)) return;

        int r = row - 1;
        int c = col - 1;
        grid[r][c] = true;

        int idx = r * n + c;
        if ((r > 0) && grid[r - 1][c]) _union((r - 1) * n + c, idx);
        if ((c < n - 1) && grid[r][c + 1]) _union(r * n + c + 1, idx);
        if ((r < n - 1) && grid[r + 1][c]) _union((r + 1) * n + c, idx);
        if ((c > 0) && grid[r][c - 1]) _union(r * n + c - 1, idx);

        openSitesCount++;
    }

    public boolean isOpen(int row, int col) {
        checkRowAndCol(row, col);

        int r = row - 1;
        int c = col - 1;

        return grid[r][c];
    }

    public boolean isFull(int row, int col) {
        checkRowAndCol(row, col);

        int r = row - 1;
        int c = col - 1;

        return isOpen(row, col) && ufFull.connected(r * n + c, size);
    }

    public int numberOfOpenSites() {
        return openSitesCount;
    }

    public boolean percolates() {
        // corner case: n=1
        if (n == 1) return isOpen(1, 1);
        // we just check if the virtual nodes are connected
        return ufPercolate.connected(size, size + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++)
                sb.append(grid[r][c] ? 1 : 0).append(" ");
            sb.append("\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(5);
        p.open(1, 2);
        p.open(2, 2);
        p.open(3, 2);
        p.open(3, 3);
        p.open(3, 4);
        p.open(4, 4);
        p.open(5, 4);
        p.open(4, 1);

        StdOut.printf("Grid configuration:%n%s", p);
        StdOut.printf("Open site count: %d%n", p.numberOfOpenSites());
        StdOut.printf("Does it percolate? %s\n", p.percolates() ?  "YES" : "NO");

        StdOut.printf("[4, 1] is full? %s%n", p.isFull(4, 1) ? "YES" : "NO");
        StdOut.printf("[3, 4] is full? %s%n", p.isFull(3, 4) ? "YES" : "NO");
    }
}
