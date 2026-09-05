import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Computes count, mean, minimum, and maximum for one CSV numeric column. */
public final class CsvColumnStats {
    public record Summary(int count, int rejected, double mean, double minimum, double maximum) {}

    public static Summary summarize(List<String> lines, int column) {
        if (column < 0) {
            throw new IllegalArgumentException("column must be non-negative");
        }
        double sum = 0.0;
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int accepted = 0;
        int rejected = 0;
        for (String line : lines) {
            String[] fields = line.split(",", -1);
            if (column >= fields.length) {
                rejected++;
                continue;
            }
            try {
                double value = Double.parseDouble(fields[column].trim());
                if (!Double.isFinite(value)) {
                    rejected++;
                    continue;
                }
                sum += value;
                minimum = Math.min(minimum, value);
                maximum = Math.max(maximum, value);
                accepted++;
            } catch (NumberFormatException error) {
                rejected++;
            }
        }
        if (accepted == 0) {
            return new Summary(0, rejected, 0.0, Double.NaN, Double.NaN);
        }
        return new Summary(accepted, rejected, sum / accepted, minimum, maximum);
    }

    public static void main(String[] args) {
        List<String> rows = new ArrayList<>(List.of("Ada,91.5", "Linus,88", "bad", "Grace,94.5"));
        Summary summary = summarize(rows, 1);
        System.out.printf(Locale.ROOT, "count=%d rejected=%d mean=%.2f range=[%.1f, %.1f]%n",
                summary.count(), summary.rejected(), summary.mean(), summary.minimum(), summary.maximum());
    }
}
