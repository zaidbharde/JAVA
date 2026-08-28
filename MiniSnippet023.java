public class MiniSnippet023 {
    public static void main(String[] args) {
        boolean prime = 23 > 1;
        for (int d = 2; d * d <= 23; d++) if (23 % d == 0) prime = false;
        System.out.println(prime);
    }
}
