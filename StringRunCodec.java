/** Encodes repeated characters and safely decodes the compact representation. */
public final class StringRunCodec {
    private StringRunCodec() {}

    public static String encode(String input) {
        if (input == null || input.isEmpty()) return "";
        StringBuilder encoded = new StringBuilder();
        int runStart = 0;
        for (int i = 1; i <= input.length(); i++) {
            if (i == input.length() || input.charAt(i) != input.charAt(runStart)) {
                encoded.append(i - runStart).append(':').append(input.charAt(runStart));
                runStart = i;
            }
        }
        return encoded.toString();
    }

    public static String decode(String encoded) {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < encoded.length();) {
            int separator = encoded.indexOf(':', i);
            if (separator < 1 || separator + 1 >= encoded.length()) throw new IllegalArgumentException("bad encoding");
            int count = Integer.parseInt(encoded.substring(i, separator));
            if (count < 1) throw new IllegalArgumentException("bad count");
            decoded.append(String.valueOf(encoded.charAt(separator + 1)).repeat(count));
            i = separator + 2;
        }
        return decoded.toString();
    }

    public static void main(String[] args) {
        String encoded = encode("aaabbc");
        System.out.println(encoded + " -> " + decode(encoded));
    }
}
