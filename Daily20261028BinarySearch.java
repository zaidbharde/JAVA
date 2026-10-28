/** Iterative binary search for a value in an ascending integer array. */
public final class Daily20261028BinarySearch {
    private Daily20261028BinarySearch() { }
    public static int indexOf(int[] sorted, int target) {
        if (sorted == null) throw new IllegalArgumentException("array is null");
        int low = 0, high = sorted.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (sorted[mid] == target) return mid;
            if (sorted[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }
    public static void main(String[] args) { System.out.println(indexOf(new int[]{2, 5, 8, 11}, 8)); }
}
