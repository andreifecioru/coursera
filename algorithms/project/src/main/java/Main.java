import com.algos.uf.IUnionFind;
import com.algos.uf.UnionFind;

public class Main {
    public static void main(String[] args) {
//        IUnionFind uf = UnionFind.withQuickFind(10);
//        IUnionFind uf = UnionFind.withQuickUnion(10);
        IUnionFind uf = UnionFind.withWeightedQuickUnion(10);

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

        assert(uf.connected(0, 7));
        assert(uf.connected(3, 9));
        assert(!uf.connected(0, 9));

        System.out.println(uf);
    }
}
