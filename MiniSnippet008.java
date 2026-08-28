public class MiniSnippet008 {
    public static void main(String[] args) {
        boolean prime = 8 > 1;
        for (int d = 2; d * d <= 8; d++) if (8 % d == 0) prime = false;
        System.out.println(prime);
    }
}
