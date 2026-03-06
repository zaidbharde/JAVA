import java.util.Scanner;
import java.util.Random;

public class GuessGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        int number = rand.nextInt(100) + 1; // random 1-100
        int guess;
        int attempts = 0;

        System.out.println("🎯 Guess the number between 1 and 100");

        do {
            System.out.print("Enter your guess: ");
            guess = sc.nextInt();
            attempts++;

            if (guess > number) {
                System.out.println("Too high! ⬆");
            } else if (guess < number) {
                System.out.println("Too low! ⬇");
            } else {
                System.out.println("🎉 Correct! You guessed in " + attempts + " attempts.");
            }

        } while (guess != number);

        sc.close();
    }
}
