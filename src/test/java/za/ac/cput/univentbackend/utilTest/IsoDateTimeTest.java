package za.ac.cput.univentbackend.utilTest;

import org.junit.jupiter.api.Test;
import za.ac.cput.util.IsoDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IsoDateTimeTest {
    @Test
    void formatsIsoAndLegacyEventTimestampsAsIso8601() {
        assertEquals("2026-08-01T10:00:00", IsoDateTime.format("2026-08-01T10:00:00"));
        assertEquals("2026-08-01T10:00:00", IsoDateTime.format("2026:08- 01T10:00:00"));
    }
}
