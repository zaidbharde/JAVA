import java.util.Scanner;

public class TimeConverter {

    public static String toHHMMSS(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter time in seconds: ");
        int seconds = sc.nextInt();

        String formatted = toHHMMSS(seconds);
        System.out.println("Formatted Time: " + formatted);
    }
}
