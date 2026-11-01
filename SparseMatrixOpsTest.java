import java.util.Arrays;

/** Dependency-free executable regression tests for SparseMatrixOps. */
public final class SparseMatrixOpsTest {
    private static int assertions;

    public static void main(String[] args) {
        testEmptyDimensions();
        testAdditionAndCancellation();
        testSparseMultiplication();
        testDenseReferenceCases();
        testInvalidInputs();
        System.out.println("SparseMatrixOpsTest: PASS (" + assertions + " assertions)");
    }

    private static void testEmptyDimensions() {
        SparseMatrixOps zeroByThree = new SparseMatrixOps(0, 3);
        equal(0, zeroByThree.rows(), "zero rows");
        equal(3, zeroByThree.columns(), "preserve columns for empty matrix");
        equal(0, zeroByThree.toDense().length, "empty dense row count");
        SparseMatrixOps product = zeroByThree.multiply(new SparseMatrixOps(3, 2));
        equal(0, product.rows(), "empty product rows");
        equal(2, product.columns(), "empty product columns");
        equal(0, new SparseMatrixOps(3, 0).multiply(new SparseMatrixOps(0, 4)).nonZeroCount(), "zero inner dimension");
        expect(IllegalArgumentException.class, () -> new SparseMatrixOps(-1, 0), "negative dimensions");
    }

    private static void testAdditionAndCancellation() {
        SparseMatrixOps a = SparseMatrixOps.fromDense(new int[][] {{1, 0, 5}, {0, 2, 0}});
        SparseMatrixOps b = SparseMatrixOps.fromDense(new int[][] {{-1, 4, -5}, {0, 3, 0}});
        SparseMatrixOps sum = a.add(b);
        matrixEquals(new int[][] {{0, 4, 0}, {0, 5, 0}}, sum.toDense(), "addition");
        equal(2, sum.nonZeroCount(), "cancelled zeros not stored");
        expect(IllegalArgumentException.class,
                () -> a.add(new SparseMatrixOps(3, 2)), "addition shape mismatch");
        sum.set(0, 1, 0);
        equal(1, sum.nonZeroCount(), "setting zero removes entry");
    }

    private static void testSparseMultiplication() {
        SparseMatrixOps a = SparseMatrixOps.fromDense(new int[][] {
            {1, 0, 2}, {0, 3, 0}
        });
        SparseMatrixOps b = SparseMatrixOps.fromDense(new int[][] {
            {0, 4}, {5, 0}, {0, 6}
        });
        matrixEquals(new int[][] {{0, 16}, {15, 0}}, a.multiply(b).toDense(), "sparse product");
        expect(IllegalArgumentException.class,
                () -> a.multiply(new SparseMatrixOps(2, 1)), "multiplication shape mismatch");
    }

    private static void testDenseReferenceCases() {
        int[][] left = {{2, -1, 0}, {4, 3, 1}, {0, 0, 0}};
        int[][] right = {{1, 2}, {-2, 3}, {5, 0}};
        int[][] expected = denseMultiply(left, right);
        matrixEquals(expected, SparseMatrixOps.fromDense(left)
                .multiply(SparseMatrixOps.fromDense(right)).toDense(), "dense reference product");
        matrixEquals(new int[][] {{2, 1, 0}, {4, 3, 1}, {0, 0, 0}},
                SparseMatrixOps.fromDense(left).add(SparseMatrixOps.fromDense(new int[][] {
                    {0, 2, 0}, {0, 0, 0}, {0, 0, 0}
                })).toDense(), "dense reference addition");
    }

    private static void testInvalidInputs() {
        expect(IllegalArgumentException.class, () -> SparseMatrixOps.fromDense(null), "null dense matrix");
        expect(IllegalArgumentException.class,
                () -> SparseMatrixOps.fromDense(new int[][] {{1}, {2, 3}}), "ragged matrix");
        SparseMatrixOps matrix = new SparseMatrixOps(1, 1);
        expect(IndexOutOfBoundsException.class, () -> matrix.get(1, 0), "row bounds");
        expect(IndexOutOfBoundsException.class, () -> matrix.set(0, 1, 2), "column bounds");
        expect(IllegalArgumentException.class, () -> matrix.add(null), "null operand");
        expect(IllegalArgumentException.class, () -> matrix.multiply(null), "null operand");
    }

    private static int[][] denseMultiply(int[][] a, int[][] b) {
        int[][] result = new int[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int k = 0; k < b.length; k++) {
                for (int j = 0; j < b[0].length; j++) result[i][j] += a[i][k] * b[k][j];
            }
        }
        return result;
    }

    private static void matrixEquals(int[][] expected, int[][] actual, String message) {
        equal(expected.length, actual.length, message + " row count");
        for (int i = 0; i < expected.length; i++) {
            if (!Arrays.equals(expected[i], actual[i])) {
                throw new AssertionError(message + " row " + i + ": expected " + Arrays.toString(expected[i])
                        + " but got " + Arrays.toString(actual[i]));
            }
            assertions++;
        }
    }

    private static void equal(int expected, int actual, String message) {
        assertions++;
        if (expected != actual) throw new AssertionError(message + ": expected " + expected + " but got " + actual);
    }

    private static void expect(Class<? extends Throwable> type, Runnable action, String message) {
        assertions++;
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError(message + ": expected " + type.getSimpleName() + " but got " + error, error);
        }
        throw new AssertionError(message + ": expected " + type.getSimpleName());
    }
}
