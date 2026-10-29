/** Finds the median of two ascending integer arrays in logarithmic time. */
public final class Daily20261028Extra019MedianSortedArrays {
    private Daily20261028Extra019MedianSortedArrays() { }

    /** Rejects null, unsorted, or jointly empty input arrays. */
    public static double median(int[] first, int[] second) {
        if (first == null || second == null)
            throw new IllegalArgumentException("arrays must not be null");
        if (first.length == 0 && second.length == 0)
            throw new IllegalArgumentException("at least one value is required");
        requireSorted(first);
        requireSorted(second);
        if (first.length > second.length) return median(second, first);

        int leftSize = (first.length + second.length + 1) / 2;
        int low = 0, high = first.length;
        while (low <= high) {
            int cutFirst = low + (high - low) / 2;
            int cutSecond = leftSize - cutFirst;
            if (cutFirst < first.length && cutSecond > 0
                    && second[cutSecond - 1] > first[cutFirst]) {
                low = cutFirst + 1;
            } else if (cutFirst > 0 && cutSecond < second.length
                    && first[cutFirst - 1] > second[cutSecond]) {
                high = cutFirst - 1;
            } else {
                int leftMax;
                if (cutFirst == 0) leftMax = second[cutSecond - 1];
                else if (cutSecond == 0) leftMax = first[cutFirst - 1];
                else leftMax = Math.max(first[cutFirst - 1], second[cutSecond - 1]);
                if (((first.length + second.length) & 1) == 1) return leftMax;
                int rightMin;
                if (cutFirst == first.length) rightMin = second[cutSecond];
                else if (cutSecond == second.length) rightMin = first[cutFirst];
                else rightMin = Math.min(first[cutFirst], second[cutSecond]);
                return ((long) leftMax + rightMin) / 2.0;
            }
        }
        throw new IllegalArgumentException("arrays must be sorted in ascending order");
    }

    private static void requireSorted(int[] values) {
        for (int i = 1; i < values.length; i++)
            if (values[i] < values[i - 1])
                throw new IllegalArgumentException("arrays must be sorted in ascending order");
    }

    public static void main(String[] args) {
        if (median(new int[]{1, 3}, new int[]{2}) != 2.0
                || median(new int[]{1, 2}, new int[]{3, 4}) != 2.5
                || median(new int[]{Integer.MIN_VALUE}, new int[]{Integer.MAX_VALUE}) != -0.5)
            throw new AssertionError("median self-check failed");
        try {
            median(new int[0], new int[0]);
            throw new AssertionError("empty input accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("MedianSortedArrays self-check passed");
    }
}
