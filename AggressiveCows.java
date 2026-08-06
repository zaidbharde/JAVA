import java.util.Arrays;

public class AggressiveCows {
    public static int maxMinDistance(int[] stalls, int cows) {
        Arrays.sort(stalls);
        int low = 1, high = stalls[stalls.length - 1] - stalls[0], result = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (canPlace(stalls, cows, mid)) {
                result = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }

    private static boolean canPlace(int[] stalls, int cows, int minDist) {
        int count = 1, lastPos = stalls[0];
        for (int i = 1; i < stalls.length; i++) {
            if (stalls[i] - lastPos >= minDist) {
                count++;
                lastPos = stalls[i];
            }
        }
        return count >= cows;
    }

    public static void main(String[] args) {
        int[] stalls = {1, 2, 4, 8, 9};
        int cows = 3;
        System.out.println("Max min distance: " + maxMinDistance(stalls, cows));
    }
}
