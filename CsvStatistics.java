import java.util.ArrayList;
import java.util.List;

/** Computes mean, median, and population standard deviation for CSV columns. */
public final class CsvStatistics {
    public record Summary(double mean, double median, double deviation, int count) {}

    public static Summary summarize(String csvColumn) {
        List<Double> values = new ArrayList<>();
        for (String token : csvColumn.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) values.add(Double.parseDouble(trimmed));
        }
        if (values.isEmpty()) throw new IllegalArgumentException("column has no numbers");
        values.sort(Double::compareTo);
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        double mean = sum / values.size();
        double squared = 0;
        for (double value : values) squared += Math.pow(value - mean, 2);
        double median = values.size() % 2 == 1
                ? values.get(values.size() / 2)
                : (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2;
        return new Summary(mean, median, Math.sqrt(squared / values.size()), values.size());
    }

    public static void main(String[] args) {
        System.out.println(summarize("4, 8, 15, 16, 23, 42"));
    }
}
