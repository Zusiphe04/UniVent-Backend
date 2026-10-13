package za.ac.cput.dtos;

public record NotificationPreferencesRequest(
        boolean emailReminders,
        boolean eventUpdates
) {
}
