import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Sparse integer matrix backed by one column-to-value map per row. */
public final class SparseMatrixOps {
    private final int rowCount;
    private final int columnCount;
    private final Map<Integer, Integer>[] rows;

    @SuppressWarnings("unchecked")
    public SparseMatrixOps(int rowCount, int columnCount) {
        if (rowCount < 0 || columnCount < 0) {
            throw new IllegalArgumentException("Matrix dimensions must be non-negative");
        }
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.rows = (Map<Integer, Integer>[]) new Map<?, ?>[rowCount];
        for (int row = 0; row < rowCount; row++) {
            rows[row] = new HashMap<>();
        }
    }

    public int rows() { return rowCount; }
    public int columns() { return columnCount; }

    /** Returns zero for an entry that is not stored. */
    public int get(int row, int column) {
        checkIndex(row, column);
        return rows[row].getOrDefault(column, 0);
    }

    /** Stores nonzero values and removes entries set to zero. */
    public void set(int row, int column, int value) {
        checkIndex(row, column);
        if (value == 0) rows[row].remove(column);
        else rows[row].put(column, value);
    }

    /** Number of stored nonzero entries. */
    public int nonZeroCount() {
        int count = 0;
        for (Map<Integer, Integer> row : rows) count += row.size();
        return count;
    }

    /** Read-only view of the nonzero entries in a row. */
    public Map<Integer, Integer> nonZeroEntriesInRow(int row) {
        if (row < 0 || row >= rowCount) throw new IndexOutOfBoundsException("row: " + row);
        return Collections.unmodifiableMap(rows[row]);
    }

    public SparseMatrixOps add(SparseMatrixOps other) {
        if (other == null || rowCount != other.rowCount || columnCount != other.columnCount) {
            throw new IllegalArgumentException("Matrix dimensions must match for addition");
        }
        SparseMatrixOps result = new SparseMatrixOps(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            result.rows[row].putAll(rows[row]);
            for (Map.Entry<Integer, Integer> entry : other.rows[row].entrySet()) {
                int column = entry.getKey();
                int sum = result.rows[row].getOrDefault(column, 0) + entry.getValue();
                if (sum == 0) result.rows[row].remove(column);
                else result.rows[row].put(column, sum);
            }
        }
        return result;
    }

    /** Sparse row-by-row multiplication; absent values are treated as zero. */
    public SparseMatrixOps multiply(SparseMatrixOps other) {
        if (other == null || columnCount != other.rowCount) {
            throw new IllegalArgumentException("Incompatible dimensions for multiplication");
        }
        SparseMatrixOps result = new SparseMatrixOps(rowCount, other.columnCount);
        for (int row = 0; row < rowCount; row++) {
            Map<Integer, Integer> output = result.rows[row];
            for (Map.Entry<Integer, Integer> left : rows[row].entrySet()) {
                for (Map.Entry<Integer, Integer> right : other.rows[left.getKey()].entrySet()) {
                    int column = right.getKey();
                    int value = output.getOrDefault(column, 0) + left.getValue() * right.getValue();
                    if (value == 0) output.remove(column);
                    else output.put(column, value);
                }
            }
        }
        return result;
    }

    public int[][] toDense() {
        int[][] dense = new int[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (Map.Entry<Integer, Integer> entry : rows[row].entrySet()) {
                dense[row][entry.getKey()] = entry.getValue();
            }
        }
        return dense;
    }

    public static SparseMatrixOps fromDense(int[][] dense) {
        if (dense == null) throw new IllegalArgumentException("Matrix must not be null");
        int columns = dense.length == 0 ? 0 : requireRectangular(dense);
        SparseMatrixOps result = new SparseMatrixOps(dense.length, columns);
        for (int row = 0; row < dense.length; row++) {
            for (int column = 0; column < columns; column++) {
                if (dense[row][column] != 0) result.set(row, column, dense[row][column]);
            }
        }
        return result;
    }

    private static int requireRectangular(int[][] dense) {
        if (dense[0] == null) throw new IllegalArgumentException("Rows must not be null");
        int columns = dense[0].length;
        for (int[] row : dense) {
            if (row == null || row.length != columns) {
                throw new IllegalArgumentException("Matrix must be rectangular");
            }
        }
        return columns;
    }

    private void checkIndex(int row, int column) {
        if (row < 0 || row >= rowCount || column < 0 || column >= columnCount) {
            throw new IndexOutOfBoundsException("matrix index: (" + row + ", " + column + ")");
        }
    }
}
