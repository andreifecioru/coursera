package com.algos.uf;

class QuickFind implements IUnionFind {
    private final int[] id;

    QuickFind(int size) {
        id = new int[size];
        for (int i = 0; i < size; i++) {
            id[i] = i;
        }
    }

    @Override
    public void union(int p, int q) {
        if (!connected(p, q)) {
            int id_p = id[p];
            int id_q = id[q];
            for (int i = 0; i < id.length; i++) {
                if (id[i] == id_p) {
                    id[i] = id_q;
                }
            }

            System.out.println(String.format("Connected %d with %d: %s", p, q, this));
        } else {
            System.out.println(String.format("%d and %d already connected.", p, q));
        }
    }
    @Override
    public boolean connected(int p, int q) {
        return id[p] == id[q];
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
