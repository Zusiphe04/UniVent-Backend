package za.ac.cput.service;

import za.ac.cput.util.Helper;

/**
 * Normalizes GET /api/events date bounds.
 * Stored event {@code dateTime} values are ISO-8601 strings such as {@code 2026-08-15T10:00:00}.
 * Frontend {@code from}/{@code to} filters are often date-only ({@code yyyy-MM-dd}).
 */
public final class EventFilter {

    private static final String DATE_ONLY = "\\d{4}-\\d{2}-\\d{2}";

    private EventFilter() {
    }

    public static String normalizeFrom(String from) {
        if (Helper.isNullOrEmpty(from)) {
            return null;
        }
        String value = from.trim();
        if (value.matches(DATE_ONLY)) {
            return value + "T00:00:00";
        }
        return value;
    }

    /** Inclusive end of day when {@code to} is a calendar date. */
    public static String normalizeTo(String to) {
        if (Helper.isNullOrEmpty(to)) {
            return null;
        }
        String value = to.trim();
        if (value.matches(DATE_ONLY)) {
            return value + "T23:59:59";
        }
        return value;
    }

    public static void validateRange(String fromBound, String toBound) {
        if (fromBound != null && toBound != null && fromBound.compareTo(toBound) > 0) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }
    }
}
