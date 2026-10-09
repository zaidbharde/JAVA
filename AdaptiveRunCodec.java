/** Run-length codec that uses a compact delimiter-free representation for digits. */
public final class AdaptiveRunCodec {
    private AdaptiveRunCodec() { }

    public static String encode(String input) {
        if (input == null || input.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        int start = 0;
        for (int end = 1; end <= input.length(); end++) {
            if (end == input.length() || input.charAt(end) != input.charAt(start)) {
                appendRun(output, end - start, input.charAt(start));
                start = end;
            }
        }
        return output.toString();
    }

    private static void appendRun(StringBuilder output, int count, char value) {
        if (output.length() > 0) output.append('/');
        output.append(count).append('#').append(Integer.toHexString(value));
    }

    public static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        for (String run : encoded.split("/")) {
            String[] parts = run.split("#", -1);
            if (parts.length != 2) throw new IllegalArgumentException("invalid run: " + run);
            int count = Integer.parseInt(parts[0]);
            int codePoint = Integer.parseInt(parts[1], 16);
            if (count < 1 || codePoint < 0 || codePoint > Character.MAX_VALUE) {
                throw new IllegalArgumentException("invalid run values");
            }
            result.append(String.valueOf((char) codePoint).repeat(count));
        }
        return result.toString();
    }
}
