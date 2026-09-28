import java.util.ArrayDeque;

/** Finds the shortest four-direction path through a rectangular maze. */
public final class MazeShortestPath {
    private MazeShortestPath() {}

    public static int distance(char[][] maze, int startRow, int startCol, int goalRow, int goalCol) {
        int rows = maze.length;
        int cols = maze[0].length;
        int[][] distance = new int[rows][cols];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {startRow, startCol});
        distance[startRow][startCol] = 1;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.remove();
            if (cell[0] == goalRow && cell[1] == goalCol) return distance[cell[0]][cell[1]] - 1;
            for (int[] direction : directions) {
                int row = cell[0] + direction[0];
                int col = cell[1] + direction[1];
                if (row >= 0 && row < rows && col >= 0 && col < cols
                        && maze[row][col] != '#' && distance[row][col] == 0) {
                    distance[row][col] = distance[cell[0]][cell[1]] + 1;
                    queue.add(new int[] {row, col});
                }
            }
        }
        return -1;
    }
}
