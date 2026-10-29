/** Quotes one CSV field when required by its delimiter or record characters. */
public final class Daily20261028Extra002CsvQuoteField {
    private Daily20261028Extra002CsvQuoteField() { }

    /**
     * Returns a CSV-safe representation of {@code value}, using double quotes
     * and doubling embedded quotes. The delimiter must be a single valid CSV character.
     */
    public static String quote(String value, char delimiter) {
        if (value == null) throw new IllegalArgumentException("value is null");
        if (delimiter == '"' || delimiter == '\r' || delimiter == '\n')
            throw new IllegalArgumentException("invalid delimiter");
        boolean needed = value.isEmpty();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == delimiter || c == '"' || c == '\r' || c == '\n') needed = true;
        }
        if (!needed) return value;
        StringBuilder result = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"') result.append('"');
            result.append(c);
        }
        return result.append('"').toString();
    }

    public static void main(String[] args) {
        assert quote("plain", ',').equals("plain");
        assert quote("", ',').equals("\"\"");
        assert quote("a,b", ',').equals("\"a,b\"");
        assert quote("a;b", ';').equals("\"a;b\"");
        assert quote("say \"hi\"", ',').equals("\"say \"\"hi\"\"\"");
        assert quote("line\nbreak", ',').equals("\"line\nbreak\"");
        System.out.println("CsvQuoteField self-check passed");
    }
}
