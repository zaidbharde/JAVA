package com.zaidbharde.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Merges overlapping half-open intervals in ascending order. */
public final class IntervalMerger {
    public record Interval(int start, int end) {
        public Interval {
            if (start > end) {
                throw new IllegalArgumentException("start must not exceed end");
            }
        }
    }

    private IntervalMerger() {
    }

    public static List<Interval> merge(List<Interval> input) {
        if (input.isEmpty()) {
            return List.of();
        }
        List<Interval> sorted = new ArrayList<>(input);
        sorted.sort(Comparator.comparingInt(Interval::start));
        List<Interval> result = new ArrayList<>();
        Interval current = sorted.get(0);
        for (int index = 1; index < sorted.size(); index++) {
            Interval next = sorted.get(index);
            if (next.start() <= current.end()) {
                current = new Interval(current.start(), Math.max(current.end(), next.end()));
            } else {
                result.add(current);
                current = next;
            }
        }
        result.add(current);
        return List.copyOf(result);
    }
}
