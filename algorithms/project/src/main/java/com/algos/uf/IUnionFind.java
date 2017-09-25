package com.algos.uf;

public interface IUnionFind {
    void union(int p, int q);
    boolean connected(int p, int q);
}
