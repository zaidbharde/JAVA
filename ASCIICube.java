public class ASCIICube {
    // Extract magic numbers into constants
    private static final int DELAY_MS = 150; 
    
    // Improved frames to simulate a crude spinning/shifting motion
    private static final String[] FRAMES = {
        "   +----+\n  /    /|\n +----+ |\n |    | +\n |    |/\n +----+",
        "    +----+\n   /    /|\n  +----+ |\n  |    | +\n  |    |/\n  +----+",
        "     +----+\n    /    /|\n   +----+ |\n   |    | +\n   |    |/\n   +----+",
        "    +----+\n   /    /|\n  +----+ |\n  |    | +\n  |    |/\n  +----+"
    };

    public static void main(String[] args) {
        // Run the animation
        playAnimation();
    }

    private static void playAnimation() {
        try {
            while (true) {
                for (String frame : FRAMES) {
                    clearScreen();
                    System.out.println(frame);
                    Thread.sleep(DELAY_MS);
                }
            }
        } catch (InterruptedException e) {
            // Properly handle the thread interruption
            Thread.currentThread().interrupt();
            System.out.println("\nAnimation stopped.");
        }
    }

    private static void clearScreen() {
        // ANSI escape code to clear screen and move cursor to top-left
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
