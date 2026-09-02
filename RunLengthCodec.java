import java.util.ArrayList;
import java.util.List;

/** Small run-length encoder/decoder for strings containing any UTF-16 characters. */
public final class RunLengthCodec {
    private RunLengthCodec() {
    }

    public static String encode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        StringBuilder encoded = new StringBuilder();
        int start = 0;
        while (start < input.length()) {
            int end = start + 1;
            while (end < input.length() && input.charAt(end) == input.charAt(start)) {
                end++;
            }
            encoded.append(end - start).append(':').append(input.charAt(start));
            if (end < input.length()) {
                encoded.append('|');
            }
            start = end;
        }
        return encoded.toString();
    }

    public static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }
        StringBuilder decoded = new StringBuilder();
        for (String token : encoded.split("\\|", -1)) {
            int separator = token.indexOf(':');
            if (separator <= 0 || separator == token.length() - 1) {
                throw new IllegalArgumentException("Malformed token: " + token);
            }
            int count = Integer.parseInt(token.substring(0, separator));
            if (count < 1) {
                throw new IllegalArgumentException("Run length must be positive");
            }
            char value = token.charAt(separator + 1);
            decoded.append(String.valueOf(value).repeat(count));
        }
        return decoded.toString();
    }

    public static List<String> runs(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result;
        }
        int start = 0;
        for (int i = 1; i <= input.length(); i++) {
            if (i == input.length() || input.charAt(i) != input.charAt(start)) {
                result.add(input.substring(start, i));
                start = i;
            }
        }
        return result;
    }
}

// Example: RunLengthCodec.decode(RunLengthCodec.encode("aaabbc")) returns "aaabbc".
