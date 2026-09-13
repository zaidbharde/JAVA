package com.zaidbharde.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Selects entries proportionally while avoiding consecutive repeats when possible. */
public final class WeightedRoundRobin<T> {
    private final List<T> entries;
    private final int[] weights;
    private int cursor;
    private int remaining;

    public WeightedRoundRobin(List<T> entries, List<Integer> weights) {
        if (entries.isEmpty() || entries.size() != weights.size()) {
            throw new IllegalArgumentException("entries and weights must have equal non-zero size");
        }
        this.entries = List.copyOf(entries);
        this.weights = weights.stream().mapToInt(weight -> {
            if (weight <= 0) throw new IllegalArgumentException("weights must be positive");
            return weight;
        }).toArray();
    }

    public T next() {
        for (int attempts = 0; attempts < entries.size() * 2; attempts++) {
            if (remaining == 0) {
                cursor = (cursor + 1) % entries.size();
                remaining = weights[cursor];
            }
            remaining--;
            return Objects.requireNonNull(entries.get(cursor));
        }
        throw new IllegalStateException("selector made no progress");
    }

    public List<T> sample(int count) {
        List<T> result = new ArrayList<>(count);
        for (int index = 0; index < count; index++) result.add(next());
        return List.copyOf(result);
    }
}
