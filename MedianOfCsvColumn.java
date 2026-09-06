import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Extracts finite values from a CSV column and answers percentile queries. */
public final class MedianOfCsvColumn {
    private MedianOfCsvColumn() {
    }

    public static double percentile(List<String> rows, int column, double quantile) {
        if (quantile < 0 || quantile > 1 || column < 0) {
            throw new IllegalArgumentException("invalid column or quantile");
        }
        List<Double> values = new ArrayList<>();
        for (String row : rows) {
            String[] fields = row.split(",", -1);
            if (column < fields.length) {
                try {
                    double value = Double.parseDouble(fields[column].trim());
                    if (Double.isFinite(value)) values.add(value);
                } catch (NumberFormatException ignored) {
                    // Malformed cells are excluded from the numeric sample.
                }
            }
        }
        if (values.isEmpty()) return Double.NaN;
        Collections.sort(values);
        double position = quantile * (values.size() - 1);
        int lower = (int) Math.floor(position);
        int upper = (int) Math.ceil(position);
        double fraction = position - lower;
        return values.get(lower) + fraction * (values.get(upper) - values.get(lower));
    }
}
