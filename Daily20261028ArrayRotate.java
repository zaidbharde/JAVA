/** In-place right rotation of an integer array. */
public final class Daily20261028ArrayRotate {
    private Daily20261028ArrayRotate() { }
    public static void rotate(int[] values, int distance) {
        if (values == null) throw new IllegalArgumentException("array is null");
        if (values.length < 2) return;
        int k = ((distance % values.length) + values.length) % values.length;
        reverse(values, 0, values.length - 1);
        reverse(values, 0, k - 1);
        reverse(values, k, values.length - 1);
    }
    private static void reverse(int[] a, int left, int right) {
        while (left < right) { int t = a[left]; a[left++] = a[right]; a[right--] = t; }
    }
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5}; rotate(a, 2);
        for (int n : a) System.out.print(n + " ");
    }
}
