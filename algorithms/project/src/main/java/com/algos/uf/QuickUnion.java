package com.algos.uf;

class QuickUnion implements IUnionFind {
    private final int[] id;

    QuickUnion(int size) {
        id = new int[size];
        for (int i = 0; i < size; i++) {
            id[i] = i;
        }
    }

    private int root(int p) {
        while (id[p] != p) {
            p = id[p];
        }
        return p;
    }

    @Override
    public void union(int p, int q) {
        if (!connected(p, q)) {
            id[root(p)] = id[root(q)];
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
                sb.append(id[0]);
                break;
            default:
                int i;
                for (i = 0; i < id.length - 1; i ++) {
                    sb.append(id[i]).append(", ");
                }
                sb.append(id[i]).append("]");
                break;
        }

        return sb.toString();
    }
}
