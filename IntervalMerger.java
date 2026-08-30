import java.util.*;

public class IntervalMerger {
    public static void main(String[] args) {
        int[][] ranges = {{1, 4}, {3, 7}, {10, 12}, {11, 15}};
        Arrays.sort(ranges, Comparator.comparingInt(a -> a[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] range : ranges) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < range[0]) {
                merged.add(range.clone());
            } else {
                merged.get(merged.size() - 1)[1] =
                    Math.max(merged.get(merged.size() - 1)[1], range[1]);
            }
        }
        for (int[] range : merged) System.out.println(Arrays.toString(range));
    }
}
