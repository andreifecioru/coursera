package exercises;

import com.algos.uf.IUnionFind;

public class SocialNetwork {
    private static class WeightedQuickUnion implements IUnionFind {
        private final int[] id;
        private final int[] rank;
        private boolean isFullyConnected = false;
        private long timeStamp = 0;

        WeightedQuickUnion(int size) {
            id = new int[size];
            rank = new int[size];

            for (int i = 0; i < size; i++) {
                id[i] = i;
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

        void union(int p, int q, long ts) {
            union(p, q);
            if (isFullyConnected) {
                timeStamp = ts;
            }
        }

        long getTimeStamp() {
            if (!isFullyConnected) throw new IllegalStateException("Not fully connected.");
            return timeStamp;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void union(int p, int q) {
            if (!connected(p, q)) {
                int root_p = root(p);
                int root_q = root(q);

                if (rank[root_p] >= rank[root_q]) {
                    id[root_q] = id[root_p];
                    rank[root_p] += rank[root_q];
                    isFullyConnected = rank[root_p] == id.length;
                } else {
                    id[root_p] = id[root_q];
                    rank[root_q] += rank[root_p];
                    isFullyConnected = rank[root_q] == id.length;
                }
                System.out.println(String.format("Connected %d with %d: %s", p, q, this));
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

        public static void main(String[] args) {

            WeightedQuickUnion uf = new WeightedQuickUnion(10);

            // setup some test connections
            // 0 - 1 - 2 - 3 - 4
            // |   |   |   |   |
            // 5 - 6   7   8   9
            uf.union(0, 1, 1);
            uf.union(1, 2, 2);
            uf.union(0, 5, 3);
            uf.union(5, 6, 4);
            uf.union(2, 7, 5);
            uf.union(1, 6, 6);
            uf.union(3, 8, 7);
            uf.union(3, 4, 8);
            uf.union(4, 9, 9);
            uf.union(2, 3, 10);

            System.out.println(String.format("Time stamp is: %d", uf.getTimeStamp()));
        }
    }
}
