import java.util.Arrays;

/** Selects order statistics without sorting the complete input. */
public final class OrderStatistics {
    private OrderStatistics() {}

    public static int select(int[] values, int rank) {
        if (values == null || rank < 0 || rank >= values.length) {
            throw new IllegalArgumentException("rank outside input");
        }
        int[] work = Arrays.copyOf(values, values.length);
        int left = 0;
        int right = work.length - 1;
        while (left <= right) {
            int pivot = partition(work, left, right);
            if (pivot == rank) return work[pivot];
            if (pivot < rank) left = pivot + 1;
            else right = pivot - 1;
        }
        throw new IllegalStateException("selection did not converge");
    }

    private static int partition(int[] values, int left, int right) {
        int pivotValue = values[right];
        int store = left;
        for (int i = left; i < right; i++) {
            if (values[i] <= pivotValue) {
                swap(values, store++, i);
            }
        }
        swap(values, store, right);
        return store;
    }

    private static void swap(int[] values, int a, int b) {
        int temp = values[a];
        values[a] = values[b];
        values[b] = temp;
    }

    public static void main(String[] args) {
        System.out.println(select(new int[] {9, 1, 7, 3, 5}, 2));
    }
}
