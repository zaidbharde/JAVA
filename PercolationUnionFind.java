public class PercolationUnionFind {
    private final boolean[] open;
    private final int[] parent;
    private final int n;
    private final int top, bottom;

    public PercolationUnionFind(int n) {
        this.n = n;
        open = new boolean[n * n];
        parent = new int[n * n + 2];
        top = n * n;
        bottom = n * n + 1;
        for (int i = 0; i < parent.length; i++) parent[i] = i;
    }

    private int find(int x) {
        while (parent[x] != x) { parent[x] = parent[parent[x]]; x = parent[x]; }
        return x;
    }

    private void union(int a, int b) {
        parent[find(a)] = find(b);
    }

    public void openSite(int row, int col) {
        int idx = row * n + col;
        open[idx] = true;
        if (row == 0) union(idx, top);
        if (row == n - 1) union(idx, bottom);

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int nr = row + d[0], nc = col + d[1];
            if (nr >= 0 && nr < n && nc >= 0 && nc < n && open[nr * n + nc]) {
                union(idx, nr * n + nc);
            }
        }
    }

    public boolean percolates() {
        return find(top) == find(bottom);
    }

    public static void main(String[] args) {
        PercolationUnionFind grid = new PercolationUnionFind(3);
        grid.openSite(0, 0);
        grid.openSite(1, 0);
        System.out.println("Percolates: " + grid.percolates());
        grid.openSite(2, 0);
        System.out.println("Percolates: " + grid.percolates());
    }
}
