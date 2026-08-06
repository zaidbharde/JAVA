import java.util.*;

public class SubarraySumEqualsK {
    public static int countSubarrays(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int sum = 0, count = 0;

        for (int num : nums) {
            sum += num;
            count += prefixCount.getOrDefault(sum - k, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {1, 1, 1, 2, -1, 1, 1};
        int k = 2;
        System.out.println("Subarrays with sum " + k + ": " + countSubarrays(nums, k));
    }
}
