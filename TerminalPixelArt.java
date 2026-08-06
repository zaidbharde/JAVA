import java.io.*;

public class TerminalPixelArt {
    public static void main(String[] args) throws IOException {
        File file = new File("pixel.txt"); // Example contains ASCII art like: @@@###$$$

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (char c : line.toCharArray()) {
                    System.out.print(colorChar(c));
                }
                System.out.println();
            }
        }
    }

    static String colorChar(char c) {
        switch (c) {
            case '@': return "\u001B[31m" + c + "\u001B[0m"; // Red
            case '#': return "\u001B[32m" + c + "\u001B[0m"; // Green
            case '$': return "\u001B[34m" + c + "\u001B[0m"; // Blue
            default:  return String.valueOf(c);
        }
    }
}
