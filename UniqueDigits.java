import java.util.Scanner;

public class UniqueDigits {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a number: ");
        String number = scanner.nextLine();
        
        StringBuilder result = new StringBuilder();
        boolean[] seen = new boolean[10];  // For digits 0–9

        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (Character.isDigit(ch)) {
                int digit = ch - '0';
                if (!seen[digit]) {
                    result.append(ch);
                    seen[digit] = true;
                }
            } else {
                System.out.println("Invalid input: non-digit character found.");
                return;
            }
        }

        System.out.println("Number after eliminating repeated digits: " + result);
    }
}
