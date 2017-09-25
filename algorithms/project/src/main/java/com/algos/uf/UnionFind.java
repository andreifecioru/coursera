package com.algos.uf;

public class UnionFind {
    static public IUnionFind withQuickFind(int size) {
        return new QuickFind(size);
    }

    static public IUnionFind withQuickUnion(int size) {
        return new QuickUnion(size);
    }

    static public IUnionFind withWeightedQuickUnion(int size) {
        return new WeightedQuickUnion(size);
    }
}
