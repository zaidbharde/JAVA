import java.util.Random;
import java.util.Scanner;
import java.io.PrintStream;

public class GuessGame {
    static Integer parseGuess(String input) {
        if (input == null) return null;
        try {
            int guess = Integer.parseInt(input.trim());
            return guess >= 1 && guess <= 100 ? guess : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    static int play(Scanner input, PrintStream output, int answer) {
        if (answer < 1 || answer > 100) {
            throw new IllegalArgumentException("answer must be between 1 and 100");
        }

        int attempts = 0;
        output.println("Guess the number between 1 and 100");
        while (input.hasNextLine()) {
            output.print("Enter your guess: ");
            Integer guess = parseGuess(input.nextLine());
            if (guess == null) {
                output.println("Please enter a whole number from 1 to 100.");
                continue;
            }

            attempts++;
            if (guess > answer) {
                output.println("Too high!");
            } else if (guess < answer) {
                output.println("Too low!");
            } else {
                output.println("Correct! You guessed in " + attempts + " attempts.");
                return attempts;
            }
        }

        output.println("Input ended before the number was guessed.");
        return attempts;
    }

    public static void main(String[] args) {
        int answer = new Random().nextInt(100) + 1;
        try (Scanner input = new Scanner(System.in)) {
            play(input, System.out, answer);
        }
    }
}
