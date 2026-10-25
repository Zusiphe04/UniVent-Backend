package za.ac.cput.dtos;

import za.ac.cput.domain.AccountStatus;

public class AuthResponse {
    private String token;
    private String role;
    private String userId;
    private String name;
    private String email;
    private String accountStatus;

    public AuthResponse(String token, String role, String userId, String name, String email,
                        AccountStatus accountStatus) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.accountStatus = accountStatus.name();
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAccountStatus() { return accountStatus; }
}
