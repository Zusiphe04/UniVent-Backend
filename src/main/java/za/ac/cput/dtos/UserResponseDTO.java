package za.ac.cput.dtos;

public record UserResponseDTO(
        String id,
        String name,
        String email,
        String role,
        String accountStatus,
        String createdAt
) {
}
