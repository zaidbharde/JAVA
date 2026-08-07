import java.util.Random;

public class ZobristHashing {
    private static final int SIZE = 3;
    private static final int PIECES = 2;
    private final long[][][] table = new long[SIZE][SIZE][PIECES];

    public ZobristHashing() {
        Random rand = new Random(42);
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                for (int p = 0; p < PIECES; p++)
                    table[i][j][p] = rand.nextLong();
    }

    public long computeHash(int[][] board) {
        long hash = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != -1) {
                    hash ^= table[i][j][board[i][j]];
                }
            }
        }
        return hash;
    }

    public static void main(String[] args) {
        ZobristHashing zh = new ZobristHashing();
        int[][] board1 = {{0,1,-1},{-1,0,-1},{1,-1,-1}};
        int[][] board2 = {{0,1,-1},{-1,0,-1},{1,-1,-1}};
        int[][] board3 = {{1,0,-1},{-1,0,-1},{1,-1,-1}};

        System.out.println("Board1 hash: " + zh.computeHash(board1));
        System.out.println("Board2 hash: " + zh.computeHash(board2));
        System.out.println("Same state? " + (zh.computeHash(board1) == zh.computeHash(board2)));
        System.out.println("Board3 hash: " + zh.computeHash(board3));
    }
}
