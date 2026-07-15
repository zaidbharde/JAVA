public class ZigzagPattern {
    public static void main(String[] args) {
        int n = 3; 
        int length = 9; 

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= length; j++) {
                if (((i + j) % 4 == 0) || (i == 2 && j % 4 == 0)) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
