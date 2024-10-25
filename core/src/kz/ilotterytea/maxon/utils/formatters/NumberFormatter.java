package kz.ilotterytea.maxon.utils.formatters;

import kz.ilotterytea.maxon.MaxonConstants;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberFormatter {
    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000.0, "k");
        suffixes.put(1_000_000.0, "M");
        suffixes.put(1_000_000_000.0, "B");
        suffixes.put(1_000_000_000_000.0, "T");
        suffixes.put(1_000_000_000_000_000.0, "Qd");
        suffixes.put(1_000_000_000_000_000_000.0, "Qi");
        suffixes.put(1_000_000_000_000_000_000_000.0, "Sx");
        suffixes.put(1_000_000_000_000_000_000_000_000.0, "Sp");
        suffixes.put(1_000_000_000_000_000_000_000_000_000.0, "O");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000.0, "N");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000.0, "D");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000_000.0, "Xz");
    }

    public static String format(double value) {
        return format(value, true);
    }

    public static String format(double value, boolean decimal) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1, decimal);
        if (value < 0) return "-" + format(-value, decimal);
        if (value < 100_000.0) {
            if (!decimal || value == Math.floor(value)) return MaxonConstants.DECIMAL_FORMAT2.format(value);
            else return MaxonConstants.DECIMAL_FORMAT.format(value);
        }

        Map.Entry<Double, String> e = suffixes.floorEntry(value);
        Double divideBy = e.getKey();
        String suffix = e.getValue();

        double truncated = value / (divideBy / 10.0); //the number part of the output times 10
        return formatWithSuffix(truncated / 10d, suffix);
    }

    private static String formatWithSuffix(double value, String suffix) {
        if (value == Math.floor(value)) {
            return String.format("%.0f%s", value, suffix);
        } else {
            return String.format("%.1f%s", value, suffix);
        }
    }

    public static String pad(long value) {
        return value <= 9 && value >= 0 ? "0" + value : String.valueOf(value);
    }
}
