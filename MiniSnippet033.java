public class MiniSnippet033 {
    public static void main(String[] args) {
        boolean prime = 33 > 1;
        for (int d = 2; d * d <= 33; d++) if (33 % d == 0) prime = false;
        System.out.println(prime);
    }
}
