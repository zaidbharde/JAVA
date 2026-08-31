import java.util.Arrays;

public class HeapSort {
    public static void sort(int[] values) {
        for (int root = values.length / 2 - 1; root >= 0; root--) {
            siftDown(values, root, values.length);
        }
        for (int end = values.length - 1; end > 0; end--) {
            swap(values, 0, end);
            siftDown(values, 0, end);
        }
    }

    private static void siftDown(int[] values, int root, int size) {
        while (2 * root + 1 < size) {
            int child = 2 * root + 1;
            if (child + 1 < size && values[child] < values[child + 1]) child++;
            if (values[root] >= values[child]) return;
            swap(values, root, child);
            root = child;
        }
    }

    private static void swap(int[] values, int left, int right) {
        int temporary = values[left];
        values[left] = values[right];
        values[right] = temporary;
    }

    public static void main(String[] args) {
        int[] values = {7, 2, 9, 4, 1, 8, 3};
        sort(values);
        System.out.println(Arrays.toString(values));
    }
}
