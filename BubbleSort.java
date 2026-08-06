import java.util.Arrays;

/**
 * Bubble Sort with early-exit optimisation.
 */
public class BubbleSort {

    public static int[] sort(int[] arr) {
        int[] a = Arrays.copyOf(arr, arr.length);   // non-destructive
        int n = a.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;

            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j]     = a[j + 1];
                    a[j + 1] = tmp;
                    swapped = true;
                }
            }

            // No swaps → already sorted
            if (!swapped) break;
        }
        return a;
    }

    public static void main(String[] args) {
        int[][] tests = {
            {64, 34, 25, 12, 22, 11, 90},
            {1, 2, 3, 4, 5},               // already sorted
            {5, 4, 3, 2, 1},               // reversed
            {42},                           // single element
        };

        for (int[] test : tests) {
            System.out.println("Before: " + Arrays.toString(test));
            System.out.println("After : " + Arrays.toString(sort(test)));
            System.out.println("─".repeat(40));
        }
    }
}
