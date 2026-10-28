/** Merges two ascending integer arrays in linear time. */
public final class Daily20261028MergeSorted {
    private Daily20261028MergeSorted() { }
    public static int[] merge(int[] a, int[] b) {
        if (a == null || b == null) throw new IllegalArgumentException("arrays must not be null");
        int[] out = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) out[k++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < a.length) out[k++] = a[i++];
        while (j < b.length) out[k++] = b[j++];
        return out;
    }
    public static void main(String[] args) {
        int[] result = merge(new int[]{1, 4, 8}, new int[]{2, 3, 9});
        for (int n : result) System.out.print(n + " ");
    }
}
