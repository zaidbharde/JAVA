/** Constant-time sum queries over inclusive rectangular ranges. */
public final class Daily20261028Extra017MatrixPrefixSum {
    private final long[][] prefix;
    private final int rows, cols;

    /** Builds an immutable prefix table; rejects null, empty, or ragged matrices. */
    public Daily20261028Extra017MatrixPrefixSum(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0)
            throw new IllegalArgumentException("matrix must be non-empty");
        rows = matrix.length;
        cols = matrix[0].length;
        prefix = new long[rows + 1][cols + 1];
        for (int r = 0; r < rows; r++) {
            if (matrix[r] == null || matrix[r].length != cols)
                throw new IllegalArgumentException("matrix must be rectangular");
            for (int c = 0; c < cols; c++)
                prefix[r + 1][c + 1] = matrix[r][c] + prefix[r][c + 1]
                        + prefix[r + 1][c] - prefix[r][c];
        }
    }

    /** Returns the sum in rows [top,bottom] and columns [left,right], inclusive. */
    public long sumRegion(int top, int left, int bottom, int right) {
        if (top < 0 || left < 0 || bottom < top || right < left || bottom >= rows || right >= cols)
            throw new IndexOutOfBoundsException("invalid rectangle");
        return prefix[bottom + 1][right + 1] - prefix[top][right + 1]
                - prefix[bottom + 1][left] + prefix[top][left];
    }

    public static void main(String[] args) {
        int[][] data = {{3, 0, 1, 4}, {5, 6, 3, 2}, {1, 2, 0, 1}};
        Daily20261028Extra017MatrixPrefixSum sums = new Daily20261028Extra017MatrixPrefixSum(data);
        if (sums.sumRegion(0, 1, 1, 2) != 10 || sums.sumRegion(2, 0, 2, 3) != 4
                || sums.sumRegion(0, 0, 2, 3) != 28)
            throw new AssertionError("prefix-sum self-check failed");
        try {
            sums.sumRegion(0, 0, 3, 0);
            throw new AssertionError("expected bounds validation");
        } catch (IndexOutOfBoundsException expected) { }
        System.out.println("MatrixPrefixSum self-check passed");
    }
}
