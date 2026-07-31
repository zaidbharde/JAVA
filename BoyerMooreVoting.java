public class BoyerMooreVoting {
    public static int findMajority(int[] nums) {
        int candidate = 0, count = 0;
        for (int num : nums) {
            if (count == 0) candidate = num;
            count += (num == candidate) ? 1 : -1;
        }

        count = 0;
        for (int num : nums) if (num == candidate) count++;
        if (count > nums.length / 2) return candidate;
        throw new RuntimeException("No majority element");
    }

    public static void main(String[] args) {
        int[] nums = {2, 2, 1, 1, 1, 2, 2};
        System.out.println("Majority element: " + findMajority(nums));
    }
}
