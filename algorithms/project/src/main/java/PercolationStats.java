import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private final int n;
    private final int trials;
    private final double[] pThresholds;
    private final double _mean;
    private final double _stddev;

    public PercolationStats(int n, int trials) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive integer.");
        if (trials <= 0) throw new IllegalArgumentException("n must be positive integer.");

        this.n = n;
        this.trials = trials;

        pThresholds = new double[trials];
        for (int i = 0; i < trials; i++) {
            pThresholds[i] = runSimulation();
        }

        _mean = StdStats.mean(pThresholds);
        _stddev = StdStats.stddev(pThresholds);
    }

    private double runSimulation() {
        Percolation p = new Percolation(n);
        while (!p.percolates()) {
            p.open(StdRandom.uniform(1, n + 1), StdRandom.uniform(1, n + 1));
        }

//        StdOut.printf("Grid configuration:%n%s", p);
//        StdOut.printf("Open site count: %d%n", p.numberOfOpenSites());
//        StdOut.printf("Does it percolate? %s\n", p.percolates() ?  "YES" : "NO");

        return p.numberOfOpenSites() / (double) (n * n);
    }

    public double mean() {
        return _mean;
    }

    public double stddev() {
        return _stddev;
    }

    private double delta() {
        return (1.96 * stddev()) / Math.sqrt(trials);
    }

    public double confidenceLo() {
        return mean() - delta();
    }

    public double confidenceHi() {
        return mean() + delta();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            StdOut.print("Usage: java PercolationStats <n> <T>%n");
        } else {
            int n = Integer.parseInt(args[0]);
            int trials = Integer.parseInt(args[1]);

            Stopwatch watch = new Stopwatch();
            PercolationStats ps = new PercolationStats(n, trials);

            StdOut.printf("mean = %f%n", ps.mean());
            StdOut.printf("stddev = %f%n", ps.stddev());
            StdOut.printf("95%% confidence interval: [ %f, %f ]%n", ps.confidenceLo(), ps.confidenceHi());
            StdOut.printf("It all took: %fs%n", watch.elapsedTime());
        }
    }
}
