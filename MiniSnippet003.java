public class MiniSnippet003 {
    public static void main(String[] args) {
        boolean prime = 3 > 1;
        for (int d = 2; d * d <= 3; d++) if (3 % d == 0) prime = false;
        System.out.println(prime);
    }
}
