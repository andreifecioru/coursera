package exercises;

import com.algos.uf.IUnionFind;

public class MaxElemUnionFind {
    private static class WeightedQuickUnion implements IUnionFind {
        private final int[] id;
        private final int[] rank;
        private final int[] max;

        WeightedQuickUnion(int size) {
            id = new int[size];
            rank = new int[size];
            max = new int[size];

            for (int i = 0; i < size; i++) {
                id[i] = max[i] = i;
                rank[i] = 1;
            }
        }

        private int root(int p) {
            while (id[p] != p) {
                id[p] = id[id[p]]; // path compression
                p = id[p];
            }
            return p;
        }


        @SuppressWarnings("Duplicates")
        @Override
        public void union(int p, int q) {
            if (!connected(p, q)) {
                int root_p = root(p);
                int root_q = root(q);
                int _max;

                if (rank[root_p] >= rank[root_q]) {
                    id[root_q] = id[root_p];
                    rank[root_p] += rank[root_q];
                    _max = max[root_p] = Math.max(max[root_p], max[root_q]);
                } else {
                    id[root_p] = id[root_q];
                    rank[root_q] += rank[root_p];
                    _max = max[root_q] = Math.max(max[root_p], max[root_q]);
                }
                System.out.println(String.format("Connected %d with %d: %s (max is: %d)", p, q, this, _max));
            } else {
                System.out.println(String.format("%d and %d already connected.", p, q));
            }
        }

        @Override
        public boolean connected(int p, int q) {
            return root(p) == root(q);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            switch (id.length) {
                case 0:
                    break;
                case 1:
                    sb.append(id[0]).append("/").append(rank[0]);
                    break;
                default:
                    int i;
                    for (i = 0; i < id.length - 1; i ++) {
                        sb.append(id[i]).append("/").append(rank[i]).append(", ");
                    }
                    sb.append(id[i]).append("/").append(rank[i]).append("]");
                    break;
            }

            return sb.toString();
        }

        int find(int p) {
            return max[root(p)];
        }

        public static void main(String[] args) {

            WeightedQuickUnion uf = new WeightedQuickUnion(10);

            // setup some test connections
            // 0 - 1 - 2   3 - 4
            // |   |   |   |   |
            // 5 - 6   7   8   9
            uf.union(0, 1);
            uf.union(1, 2);
            uf.union(0, 5);
            uf.union(5, 6);
            uf.union(2, 7);
            uf.union(1, 6);
            uf.union(3, 8);
            uf.union(3, 4);
            uf.union(4, 9);

            System.out.println(String.format("find(0): %d", uf.find(0)));
            System.out.println(String.format("find(4): %d", uf.find(4)));
        }
    }
}
