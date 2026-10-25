package za.ac.cput.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.dtos.StudentProfileResponse;
import za.ac.cput.dtos.StudentProfileUpdateRequest;
import za.ac.cput.dtos.NotificationPreferencesRequest;
import za.ac.cput.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<StudentProfileResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userProfileService.getStudentProfile(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<StudentProfileResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody StudentProfileUpdateRequest request) {
        return ResponseEntity.ok(userProfileService.updateStudentProfile(authentication.getName(), request));
    }

    @PatchMapping("/me/preferences")
    public ResponseEntity<StudentProfileResponse> updatePreferences(
            Authentication authentication,
            @RequestBody NotificationPreferencesRequest request) {
        return ResponseEntity.ok(userProfileService.updatePreferences(authentication.getName(), request));
    }
}
