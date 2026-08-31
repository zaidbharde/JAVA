import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateRangeFormatter {
    public record DateRange(LocalDate start, LocalDate end) {
        public DateRange {
            if (start.isAfter(end)) throw new IllegalArgumentException("start after end");
        }

        public long inclusiveDays() {
            return ChronoUnit.DAYS.between(start, end) + 1;
        }

        public String describe() {
            return start + " to " + end + " (" + inclusiveDays() + " days)";
        }
    }

    public static DateRange parse(String value) {
        String[] pieces = value.split("/");
        if (pieces.length != 2) throw new IllegalArgumentException("expected start/end");
        return new DateRange(LocalDate.parse(pieces[0]), LocalDate.parse(pieces[1]));
    }

    public static void main(String[] args) {
        System.out.println(parse("2026-09-01/2026-09-07").describe());
    }
}
