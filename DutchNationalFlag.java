public class DutchNationalFlag {
    public static void sort(int[] arr) {
        int low = 0, mid = 0, high = arr.length - 1;

        while (mid <= high) {
            switch (arr[mid]) {
                case 0 -> { swap(arr, low++, mid++); }
                case 1 -> mid++;
                case 2 -> swap(arr, mid, high--);
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {2, 0, 1, 2, 1, 0, 0, 2, 1};
        sort(arr);
        System.out.println(java.util.Arrays.toString(arr));
    }
}
