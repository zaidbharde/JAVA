import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public final class GuessGameTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        check(GuessGame.parseGuess("1") == 1, "lower bound should be valid");
        check(GuessGame.parseGuess(" 100 ") == 100, "trimmed upper bound should be valid");
        check(GuessGame.parseGuess("42") == 42, "valid integer should parse");
        check(GuessGame.parseGuess("word") == null, "text should be rejected");
        check(GuessGame.parseGuess("") == null, "blank input should be rejected");
        check(GuessGame.parseGuess("0") == null, "zero should be rejected");
        check(GuessGame.parseGuess("101") == null, "values above the range should be rejected");
        check(GuessGame.parseGuess("999999999999") == null, "integer overflow should be rejected");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Scanner input = new Scanner("oops\n0\n101\n42\n70\n");
        int attempts = GuessGame.play(input, new PrintStream(bytes), 70);
        String output = bytes.toString();
        check(attempts == 2, "only valid guesses should count");
        check(output.contains("whole number from 1 to 100"), "invalid inputs should receive guidance");
        check(output.contains("Too low!"), "valid guesses should receive hints");
        check(output.contains("Correct! You guessed in 2 attempts."), "success should report valid attempts");

        ByteArrayOutputStream eofBytes = new ByteArrayOutputStream();
        int eofAttempts = GuessGame.play(new Scanner("25\n"), new PrintStream(eofBytes), 50);
        check(eofAttempts == 1, "EOF should preserve the number of valid attempts");
        check(eofBytes.toString().contains("Input ended"), "EOF should exit with a clear message");
        System.out.println("GuessGameTest passed");
    }
}
