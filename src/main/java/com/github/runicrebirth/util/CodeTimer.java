package com.github.runicrebirth.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Tuple;

public class CodeTimer {

    private final List<Tuple<String, Long>> timing = new ArrayList<>();

    public CodeTimer() {
        add("START");
    }

    public void add(String name) {
        timing.add(new Tuple<>(name, System.nanoTime()));
    }

    public String getOutput(String delimiter) {
        StringBuilder sb = new StringBuilder();
        long itemDelta;
        long totalDelta = 0;
        for (int i = 1; i < timing.size(); i++) {
            Tuple<String, Long> last = timing.get(i - 1);
            Tuple<String, Long> curr = timing.get(i);
            itemDelta = curr.getB() - last.getB();
            totalDelta += itemDelta;
            sb.append(String.format("%s%s%s%s%f%s%f%n",
                last.getA(), delimiter, curr.getA(), delimiter,
                itemDelta / 1_000_000d, delimiter, totalDelta / 1_000_000d));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getOutput("\t");
    }
}
