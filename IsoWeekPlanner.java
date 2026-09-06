import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/** Builds Monday-to-Sunday ISO week ranges touched by an interval. */
public final class IsoWeekPlanner {
    public record Week(LocalDate monday, LocalDate sunday) {}

    private IsoWeekPlanner() {
    }

    public static List<Week> weeksBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("interval must be ordered");
        }
        List<Week> weeks = new ArrayList<>();
        LocalDate monday = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastMonday = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        while (!monday.isAfter(lastMonday)) {
            weeks.add(new Week(monday, monday.plusDays(6)));
            monday = monday.plusWeeks(1);
        }
        return weeks;
    }
}
