import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple offline weather lookup using a static city database.
 * (Replace the map with a real API call for production use.)
 */
public class WeatherApp {

    // ── Weather record ────────────────────────────────────────────────
    record WeatherData(String city, double tempC, int humidity, String condition) {
        double tempF() { return tempC * 9 / 5 + 32; }

        @Override
        public String toString() {
            return String.format(
                """
                  ┌─────────────────────────────┐
                  │  City      : %-15s│
                  │  Condition : %-15s│
                  │  Temp      : %.1f°C / %.1f°F │
                  │  Humidity  : %d%%              │
                  └─────────────────────────────┘
                """,
                city, condition, tempC, tempF(), humidity
            );
        }
    }

    // ── Static city database ──────────────────────────────────────────
    private static final Map<String, WeatherData> DATABASE = new HashMap<>();

    static {
        DATABASE.put("london",   new WeatherData("London",    15.0, 80, "Cloudy"));
        DATABASE.put("tokyo",    new WeatherData("Tokyo",     28.0, 70, "Sunny"));
        DATABASE.put("new york", new WeatherData("New York",  22.0, 65, "Partly Cloudy"));
        DATABASE.put("paris",    new WeatherData("Paris",     18.0, 75, "Rainy"));
        DATABASE.put("sydney",   new WeatherData("Sydney",    25.0, 60, "Sunny"));
        DATABASE.put("dubai",    new WeatherData("Dubai",     40.0, 40, "Hot & Clear"));
        DATABASE.put("moscow",   new WeatherData("Moscow",    -5.0, 85, "Snowy"));
    }

    // ── Lookup ────────────────────────────────────────────────────────
    public static WeatherData getWeather(String city) {
        return DATABASE.get(city.trim().toLowerCase());
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=".repeat(40));
        System.out.println("  Simple Weather App");
        System.out.println("  Available: London, Tokyo, New York,");
        System.out.println("             Paris, Sydney, Dubai, Moscow");
        System.out.println("=".repeat(40));

        while (true) {
            System.out.print("\n  Enter city (or 'quit'): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                System.out.println("\n  Goodbye! ☀️");
                break;
            }

            WeatherData data = getWeather(input);
            if (data == null) {
                System.out.println("  ❌ City not found. Try another.");
            } else {
                System.out.println(data);
            }
        }
        scanner.close();
    }
}
