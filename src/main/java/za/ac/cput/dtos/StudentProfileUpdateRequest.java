package za.ac.cput.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentProfileUpdateRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Surname is required") String surname,
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\d{10}$", message = "Mobile number must contain 10 digits") String phoneNumber,
        @Size(max = 120, message = "Residence must be 120 characters or fewer") String residence,
        @NotBlank(message = "Faculty is required") String faculty,
        @Min(value = 1, message = "Year of study must be greater than 0") int yearOfStudy
) {
}
