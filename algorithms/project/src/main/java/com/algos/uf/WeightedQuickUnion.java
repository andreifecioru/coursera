package com.algos.uf;

class WeightedQuickUnion implements IUnionFind {
    private final int[] id;
    private final int[] rank;

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

    @Override
    public void union(int p, int q) {
        if (!connected(p, q)) {
            int root_p = root(p);
            int root_q = root(q);

            if (rank[root_p] >= rank[root_q]) {
                id[root_q] = id[root_p];
                rank[root_p] += rank[root_q];
            } else {
                id[root_p] = id[root_q];
                rank[root_q] += rank[root_p];
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
}
