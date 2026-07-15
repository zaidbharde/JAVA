public class ASCIICube {
    public static void main(String[] args) throws InterruptedException {
        String[] frames = {
            "  +----+\n /    /|\n+----+ |\n|    | +\n|    |/\n+----+",
            "  +----+\n /    /|\n+----+ |\n|    | +\n|    |/\n+----+",
            "   +--+\n  /  /|\n +--+ |\n |  | +\n |  |/\n +--+"
        };

        while (true) {
            for (String f : frames) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println(f);
                Thread.sleep(200);
            }
        }
    }
}
