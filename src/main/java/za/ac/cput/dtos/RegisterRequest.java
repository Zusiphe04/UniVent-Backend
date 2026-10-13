package za.ac.cput.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must contain 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Role is required")
    private String role;

    // Student-specific
    private String studentNumber;
    private String faculty;
    private String department;
    @Min(value = 1, message = "Year of study must be greater than 0")
    private Integer yearOfStudy;

    // Organizer-specific
    private String organizationName;
    private String organizationType;
    private String contactEmail;

    public RegisterRequest() {}

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public String getStudentNumber() { return studentNumber; }
    public String getFaculty() { return faculty; }
    public String getDepartment() { return department; }
    public Integer getYearOfStudy() { return yearOfStudy; }
    public String getOrganizationName() { return organizationName; }
    public String getOrganizationType() { return organizationType; }
    public String getContactEmail() { return contactEmail; }
}
