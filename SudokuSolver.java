/**
 * Sudoku Solver — solves any valid 9×9 puzzle using backtracking.
 * Prints the board before and after solving.
 */
public class SudokuSolver {

    private static final int SIZE  = 9;
    private static final int EMPTY = 0;

    // ── Solver ────────────────────────────────────────────────────────
    public static boolean solve(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solve(board)) return true;
                            board[row][col] = EMPTY;   // backtrack
                        }
                    }
                    return false;   // no valid number found
                }
            }
        }
        return true;   // no empty cells remain
    }

    // ── Validation ────────────────────────────────────────────────────
    private static boolean isValid(int[][] board, int row, int col, int num) {
        // Check row
        for (int j = 0; j < SIZE; j++)
            if (board[row][j] == num) return false;

        // Check column
        for (int i = 0; i < SIZE; i++)
            if (board[i][col] == num) return false;

        // Check 3×3 box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[boxRow + i][boxCol + j] == num) return false;

        return true;
    }

    // ── Display ───────────────────────────────────────────────────────
    public static void printBoard(String label, int[][] board) {
        System.out.println("\n  " + label);
        System.out.println("  ┌───────┬───────┬───────┐");

        for (int i = 0; i < SIZE; i++) {
            if (i == 3 || i == 6)
                System.out.println("  ├───────┼───────┼───────┤");

            System.out.print("  │ ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] == EMPTY ? "." : board[i][j]);
                if (j == 2 || j == 5) System.out.print(" │ ");
                else if (j < SIZE - 1) System.out.print(" ");
            }
            System.out.println(" │");
        }
        System.out.println("  └───────┴───────┴───────┘");
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        int[][] puzzle = {
            {5, 3, 0,  0, 7, 0,  0, 0, 0},
            {6, 0, 0,  1, 9, 5,  0, 0, 0},
            {0, 9, 8,  0, 0, 0,  0, 6, 0},

            {8, 0, 0,  0, 6, 0,  0, 0, 3},
            {4, 0, 0,  8, 0, 3,  0, 0, 1},
            {7, 0, 0,  0, 2, 0,  0, 0, 6},

            {0, 6, 0,  0, 0, 0,  2, 8, 0},
            {0, 0, 0,  4, 1, 9,  0, 0, 5},
            {0, 0, 0,  0, 8, 0,  0, 7, 9}
        };

        System.out.println("=".repeat(40));
        System.out.println("  Sudoku Solver");
        System.out.println("=".repeat(40));

        printBoard("Puzzle:", puzzle);

        long start = System.nanoTime();
        boolean solved = solve(puzzle);
        long elapsed = System.nanoTime() - start;

        if (solved) {
            printBoard("Solution:", puzzle);
            System.out.printf("%n  ✅ Solved in %.3f ms%n", elapsed / 1_000_000.0);
        } else {
            System.out.println("\n  ❌ No solution exists for this puzzle.");
        }
    }
}
