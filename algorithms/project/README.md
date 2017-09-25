# Q1
Modify the weighted UF union() method to check if the rank of the resulted merged tree is equal with N (the size of the problem). This means that all persons are part of the same (single) connected component. When this happens, record the timestamp of the connection that triggered this event (this uses the assumptions that connections are sorted in ascending order by their timestamp).

Code of union looks like this:
```
public void union(int p, int q, long ts) {
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

      // if are fully connected, store the ts
      if (isFullyConnected) timeStamp = ts;
    }
}
```
# Q2
Keep an additional array of length N (initialised with the index value) called max. Whenever we do a connection, what we do is to merge 2 trees (2 root nodes). In the max array we store on the index corresponding to the new root the largest between the two roots of the trees getting merged. The find() implementation is then just indexing the max[] array by the root of the node.

Code looks like this:
```
public void union(int p, int q) {
    if (!connected(p, q)) {
        int root_p = root(p);
        int root_q = root(q);

        if (rank[root_p] >= rank[root_q]) {
            id[root_q] = id[root_p];
            rank[root_p] += rank[root_q];
            max[root_p] = Math.max(max[root_p], max[root_q]);
        } else {
            id[root_p] = id[root_q];
            rank[root_q] += rank[root_p];
            max[root_q] = Math.max(max[root_p], max[root_q]);
        }
    }
}

public int find(int p) {
    return max[root(p)];
}
```

# Q3
We build upon the solution to the previous problem. We want to group adjiacent deleted items into connected components. When we delete x from S we check if x-1 or x+1 are still present; if not we issue a union(x, x+1) or union(x, x-1). We then use the find() method from the previous question to find the maximum value within the bounds of that connected component. The value of the successor method is: find(x) + 1.

Code looks like this:
```
pubic void delete(int x) {
  // connect with the previous element if previously deleted
  if (id[x-1] == -1) {
    union(x, x-1)
  }

  // connect with the next element if previously deleted
  if (id[x+1] == -1) {
    union[x, x+1]
  }
  
  // mark x as being deleted
  id[x] = -1
}

public successor(int x) {
  // we use the implementation of find() from the previous question 
  return find(x) + 1;
}
```
