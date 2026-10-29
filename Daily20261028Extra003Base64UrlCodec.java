/** Encodes and decodes URL-safe Base64 strings without padding. */
public final class Daily20261028Extra003Base64UrlCodec {
    private Daily20261028Extra003Base64UrlCodec() { }

    public static String encode(byte[] bytes) {
        if (bytes == null) throw new IllegalArgumentException("bytes is null");
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Decodes unpadded URL-safe Base64, rejecting padding and noncanonical forms. */
    public static byte[] decode(String text) {
        if (text == null) throw new IllegalArgumentException("text is null");
        if (text.indexOf('=') >= 0 || text.length() % 4 == 1)
            throw new IllegalArgumentException("invalid unpadded Base64 length or padding");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9') || c == '-' || c == '_'))
                throw new IllegalArgumentException("invalid URL-safe Base64 character at " + i);
        }
        try {
            byte[] decoded = java.util.Base64.getUrlDecoder().decode(text);
            if (!encode(decoded).equals(text))
                throw new IllegalArgumentException("noncanonical Base64 encoding");
            return decoded;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("invalid URL-safe Base64", ex);
        }
    }

    public static void main(String[] args) {
        byte[] sample = {0, 1, 2, (byte) 0xfb, (byte) 0xff};
        String encoded = encode(sample);
        assert encoded.equals("AAEC-_8");
        assert java.util.Arrays.equals(decode(encoded), sample);
        assert decode("").length == 0;
        for (String invalid : new String[] {"A", "AA=", "+w", "AB"}) {
            try { decode(invalid); throw new AssertionError("accepted: " + invalid); }
            catch (IllegalArgumentException expected) { }
        }
        System.out.println("Base64UrlCodec self-check passed");
    }
}
