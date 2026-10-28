/** Finds the first index whose value is at least the requested key. */
public final class Daily20261028LowerBound {
    private Daily20261028LowerBound() { }
    public static int find(int[] sorted, int key) {
        if (sorted == null) throw new IllegalArgumentException("array is null");
        int low = 0, high = sorted.length;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (sorted[mid] < key) low = mid + 1;
            else high = mid;
        }
        return low;
    }
    public static void main(String[] args) {
        System.out.println(find(new int[]{1, 3, 3, 7}, 3));
    }
}
