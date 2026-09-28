/** Validates UTF-8 byte sequences without decoding them into a String. */
public final class Utf8Validator {
    private Utf8Validator() {}

    public static boolean isValid(byte[] bytes) {
        int continuation = 0;
        int codePoint = 0;
        int minimum = 0;
        for (byte value : bytes) {
            int current = value & 0xFF;
            if (continuation == 0) {
                if (current < 0x80) continue;
                if (current >= 0xC2 && current <= 0xDF) {
                    continuation = 1; codePoint = current & 0x1F; minimum = 0x80;
                } else if (current >= 0xE0 && current <= 0xEF) {
                    continuation = 2; codePoint = current & 0x0F; minimum = 0x800;
                } else if (current >= 0xF0 && current <= 0xF4) {
                    continuation = 3; codePoint = current & 0x07; minimum = 0x10000;
                } else return false;
            } else {
                if (current < 0x80 || current > 0xBF) return false;
                codePoint = (codePoint << 6) | (current & 0x3F);
                if (--continuation == 0 && (codePoint < minimum || codePoint > 0x10FFFF
                        || (codePoint >= 0xD800 && codePoint <= 0xDFFF))) return false;
            }
        }
        return continuation == 0;
    }
}
