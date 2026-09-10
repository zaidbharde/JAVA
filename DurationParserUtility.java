import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses compact duration strings such as "2h 15m 4s" into milliseconds. */
public final class DurationParserUtility {
    private static final Pattern TOKEN = Pattern.compile("(\\d+(?:\\.\\d+)?)[ ]*(ms|s|m|h|d)", Pattern.CASE_INSENSITIVE);

    private DurationParserUtility() { }

    public static long parseMillis(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("duration cannot be blank");
        }
        Matcher matcher = TOKEN.matcher(input);
        long total = 0L;
        int consumed = 0;
        while (matcher.find()) {
            if (!input.substring(consumed, matcher.start()).trim().isEmpty()) {
                throw new IllegalArgumentException("invalid duration near: " + input.substring(consumed));
            }
            double amount = Double.parseDouble(matcher.group(1));
            long multiplier = multiplier(matcher.group(2));
            total = Math.addExact(total, Math.round(amount * multiplier));
            consumed = matcher.end();
        }
        if (consumed == 0 || !input.substring(consumed).trim().isEmpty()) {
            throw new IllegalArgumentException("invalid duration: " + input);
        }
        return total;
    }

    private static long multiplier(String unit) {
        return switch (unit.toLowerCase(Locale.ROOT)) {
            case "ms" -> 1L;
            case "s" -> 1_000L;
            case "m" -> 60_000L;
            case "h" -> 3_600_000L;
            case "d" -> 86_400_000L;
            default -> throw new IllegalArgumentException("unknown duration unit");
        };
    }
}
