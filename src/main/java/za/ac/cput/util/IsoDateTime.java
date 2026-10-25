package za.ac.cput.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class IsoDateTime {
    private static final DateTimeFormatter LEGACY_EVENT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy:MM- dd'T'HH:mm:ss");

    private IsoDateTime() {
    }

    public static String format(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException ignoredOffset) {
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ignoredLocal) {
                return LocalDateTime.parse(value, LEGACY_EVENT_FORMAT)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
    }
}
