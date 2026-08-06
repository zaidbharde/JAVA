public class FenwickTree {
    private final int[] tree;
    private final int n;

    public FenwickTree(int n) {
        this.n = n;
        tree = new int[n + 1];
    }

    public void update(int i, int delta) {
        for (; i <= n; i += i & (-i))
            tree[i] += delta;
    }

    public int query(int i) {
        int sum = 0;
        for (; i > 0; i -= i & (-i))
            sum += tree[i];
        return sum;
    }

    public int rangeQuery(int l, int r) {
        return query(r) - query(l - 1);
    }

    public static void main(String[] args) {
        FenwickTree ft = new FenwickTree(10);
        int[] arr = {0, 3, 2, -1, 6, 5, 4, -3, 3, 7, 2};
        for (int i = 1; i <= 10; i++) ft.update(i, arr[i]);

        System.out.println("Sum(1,5): " + ft.rangeQuery(1, 5));
        ft.update(3, 4); // add 4 to index 3
        System.out.println("Sum(1,5) after update: " + ft.rangeQuery(1, 5));
    }
}
