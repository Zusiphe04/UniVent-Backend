package za.ac.cput.util;

import java.util.regex.Pattern;

public class Helper {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().isEmpty());
    }


    public static boolean isValidEmail(String email) {
        return !isNullOrEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isPositive(int number) {
        return number > 0;
    }

    public static boolean isValidDateTime(String dateTime) {
        return !isNullOrEmpty(dateTime);
        // You can improve this later to check format like "YYYY-MM-DD HH:MM"
    }

    // Venue-specific validation methods
    public static boolean isValidVenueId(int venueId) {
        return isPositive(venueId);
    }

    public static boolean isValidVenueName(String venueName) {
        return !isNullOrEmpty(venueName);
    }

    public static boolean isValidCampus(String campus) {
        return !isNullOrEmpty(campus);
    }

    public static boolean isValidLocationDetails(String locationDetails) {
        return !isNullOrEmpty(locationDetails);
    }



    public static boolean isValidDateTime(java.time.LocalDateTime dateTime) {
        return dateTime != null && !dateTime.isBefore(java.time.LocalDateTime.now());
    }
    public static boolean isValidPhone(String phoneNumber) {
        return !isNullOrEmpty(phoneNumber) && phoneNumber.matches("^\\d{10}$");
    }

    public static boolean isValidCapacity(int capacity) {
        return isPositive(capacity);
    }
}
