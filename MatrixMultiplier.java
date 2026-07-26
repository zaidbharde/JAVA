/**
 * Matrix Multiplier — multiply two 2D matrices with full validation
 * and pretty-printed output.
 */
public class MatrixMultiplier {

    /**
     * Multiply two matrices A (m×n) and B (n×p) → result (m×p).
     *
     * @throws IllegalArgumentException if inner dimensions do not match
     */
    public static int[][] multiply(int[][] a, int[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;

        if (b.length != n)
            throw new IllegalArgumentException(
                "Incompatible dimensions: A is " + m + "×" + n +
                ", B is " + b.length + "×" + p
            );

        int[][] result = new int[m][p];

        for (int i = 0; i < m; i++)
            for (int j = 0; j < p; j++)
                for (int k = 0; k < n; k++)
                    result[i][j] += a[i][k] * b[k][j];

        return result;
    }

    // ── Display ───────────────────────────────────────────────────────
    public static void printMatrix(String label, int[][] matrix) {
        System.out.println("\n  " + label);
        System.out.println("  " + "─".repeat(30));
        for (int[] row : matrix) {
            System.out.print("  │ ");
            for (int val : row) System.out.printf("%6d", val);
            System.out.println("  │");
        }
        System.out.println("  " + "─".repeat(30));
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        int[][] A = {
            {1, 2, 3},
            {4, 5, 6}
        };
        int[][] B = {
            {7,  8},
            {9,  10},
            {11, 12}
        };

        System.out.println("=".repeat(40));
        System.out.println("  Matrix Multiplier Demo");
        System.out.println("=".repeat(40));

        printMatrix("Matrix A (2×3)", A);
        printMatrix("Matrix B (3×2)", B);

        int[][] C = multiply(A, B);
        printMatrix("Result  A × B (2×2)", C);

        // Test dimension mismatch
        try {
            multiply(A, A);
        } catch (IllegalArgumentException e) {
            System.out.println("\n  Caught: " + e.getMessage());
        }
    }
}
