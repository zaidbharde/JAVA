/** Computes edit distance while retaining only one dynamic-programming row. */
public final class EditDistanceMatrix {
    private EditDistanceMatrix() { }

    public static int distance(String left, String right) {
        if (left == null || right == null) throw new NullPointerException("strings must not be null");
        if (left.length() < right.length()) return distance(right, left);
        int[] row = new int[right.length() + 1];
        for (int column = 0; column <= right.length(); column++) row[column] = column;
        for (int i = 1; i <= left.length(); i++) {
            int diagonal = row[0];
            row[0] = i;
            for (int j = 1; j <= right.length(); j++) {
                int above = row[j];
                int substitution = diagonal + (left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1);
                row[j] = Math.min(Math.min(row[j] + 1, row[j - 1] + 1), substitution);
                diagonal = above;
            }
        }
        return row[right.length()];
    }

    public static boolean within(String left, String right, int threshold) {
        if (threshold < 0) return false;
        if (Math.abs(left.length() - right.length()) > threshold) return false;
        return distance(left, right) <= threshold;
    }
}
