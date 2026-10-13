package za.ac.cput.dtos;

public record NotificationPreferencesResponse(
        boolean emailReminders,
        boolean eventUpdates
) {
}
