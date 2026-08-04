public class KnightsTour {
    static int N = 5;
    static int[][] board = new int[N][N];
    static int[] moveX = {2, 1, -1, -2, -2, -1, 1, 2};
    static int[] moveY = {1, 2, 2, 1, -1, -2, -2, -1};

    public static boolean solve() {
        for (int[] row : board) java.util.Arrays.fill(row, -1);
        board[0][0] = 0;
        return solveUtil(0, 0, 1);
    }

    private static boolean solveUtil(int x, int y, int moveCount) {
        if (moveCount == N * N) return true;

        for (int i = 0; i < 8; i++) {
            int nx = x + moveX[i], ny = y + moveY[i];
            if (isValid(nx, ny)) {
                board[nx][ny] = moveCount;
                if (solveUtil(nx, ny, moveCount + 1)) return true;
                board[nx][ny] = -1;
            }
        }
        return false;
    }

    private static boolean isValid(int x, int y) {
        return x >= 0 && x < N && y >= 0 && y < N && board[x][y] == -1;
    }

    public static void main(String[] args) {
        if (solve()) {
            for (int[] row : board) System.out.println(java.util.Arrays.toString(row));
        } else {
            System.out.println("No solution exists");
        }
    }
}
