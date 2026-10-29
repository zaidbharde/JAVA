import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/** Returns a shortest four-neighbor route through a rectangular grid (# marks blocked cells). */
public final class Daily20261028Extra022GridShortestPath {
    private Daily20261028Extra022GridShortestPath() {}
    /** Coordinates are immutable [row, column] pairs; returns empty if the goal is unreachable. */
    public static List<List<Integer>> shortestPath(char[][] grid, int startRow, int startCol,
                                                    int goalRow, int goalCol) {
        Objects.requireNonNull(grid, "grid");
        if (grid.length == 0) throw new IllegalArgumentException("grid must not be empty");
        Objects.requireNonNull(grid[0], "grid row");
        int rows = grid.length, cols = grid[0].length;
        if (cols == 0) throw new IllegalArgumentException("grid rows must not be empty");
        for (char[] row : grid) {
            Objects.requireNonNull(row, "grid row");
            if (row.length != cols) throw new IllegalArgumentException("grid must be rectangular");
        }
        checkCell(startRow, startCol, rows, cols);
        checkCell(goalRow, goalCol, rows, cols);
        if (grid[startRow][startCol] == '#' || grid[goalRow][goalCol] == '#')
            throw new IllegalArgumentException("start and goal must be open");
        int[][] parent = new int[rows][cols];
        for (int[] row : parent) java.util.Arrays.fill(row, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int start = startRow * cols + startCol, goal = goalRow * cols + goalCol;
        parent[startRow][startCol] = start;
        queue.add(start);
        int[] dr = {1, -1, 0, 0}, dc = {0, 0, 1, -1};
        while (!queue.isEmpty() && parent[goalRow][goalCol] == -1) {
            int cell = queue.remove(), r = cell / cols, c = cell % cols;
            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i], nc = c + dc[i];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && grid[nr][nc] != '#' && parent[nr][nc] == -1) {
                    parent[nr][nc] = cell;
                    queue.add(nr * cols + nc);
                }
            }
        }
        if (parent[goalRow][goalCol] == -1) return List.of();
        List<List<Integer>> path = new ArrayList<>();
        for (int cell = goal; ; cell = parent[cell / cols][cell % cols]) {
            path.add(List.of(cell / cols, cell % cols));
            if (cell == start) break;
        }
        Collections.reverse(path);
        return List.copyOf(path);
    }
    private static void checkCell(int r, int c, int rows, int cols) {
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            throw new IllegalArgumentException("coordinate out of bounds");
    }
    public static void main(String[] args) {
        char[][] grid = {"...".toCharArray(), ".#.".toCharArray(), "...".toCharArray()};
        List<List<Integer>> path = shortestPath(grid, 0, 0, 2, 2);
        if (path.size() != 5 || !path.get(0).equals(List.of(0, 0))
                || !path.get(4).equals(List.of(2, 2))) throw new AssertionError(path);
        if (!shortestPath(new char[][] {".#.".toCharArray()}, 0, 0, 0, 2).isEmpty())
            throw new AssertionError("expected unreachable goal");
        if (shortestPath(grid, 1, 0, 1, 0).size() != 1) throw new AssertionError("same-cell path");
        System.out.println("GridShortestPath self-check passed: " + path);
    }
}
