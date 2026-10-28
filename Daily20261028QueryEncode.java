import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/** Encodes a string for use as a URL query parameter value. */
public final class Daily20261028QueryEncode {
    private Daily20261028QueryEncode() { }
    public static String encode(String value) {
        if (value == null) throw new IllegalArgumentException("value is null");
        try { return URLEncoder.encode(value, "UTF-8"); }
        catch (UnsupportedEncodingException impossible) { throw new AssertionError(impossible); }
    }
    public static void main(String[] args) { System.out.println(encode("tea & coffee")); }
}
