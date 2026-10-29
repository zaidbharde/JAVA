import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Builds UTF-8 form-encoded query strings while preserving repeated keys and order. */
public final class Daily20261028Extra007QueryStringBuilder {
    private final List<String[]> parameters = new ArrayList<>();

    public Daily20261028Extra007QueryStringBuilder add(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("query keys and values must not be null");
        }
        parameters.add(new String[] {key, value});
        return this;
    }

    public String build() {
        StringBuilder query = new StringBuilder();
        for (String[] parameter : parameters) {
            if (query.length() > 0) query.append('&');
            query.append(encode(parameter[0])).append('=').append(encode(parameter[1]));
        }
        return query.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String query = new Daily20261028Extra007QueryStringBuilder()
                .add("tag", "café & tea").add("tag", "two words").build();
        if (!"tag=caf%C3%A9+%26+tea&tag=two+words".equals(query)) {
            throw new AssertionError(query);
        }
        try {
            new Daily20261028Extra007QueryStringBuilder().add("key", null);
            throw new AssertionError("null value accepted");
        } catch (IllegalArgumentException expected) { }
        if (!new Daily20261028Extra007QueryStringBuilder().build().isEmpty()) {
            throw new AssertionError("empty builder should produce an empty query");
        }
        System.out.println(query);
    }
}
