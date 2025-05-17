package spring.charityapp.dto;

public record AuthResponse(
        String access_token,
        String refresh_token,
        int userId,
        String fullName,
        String role
) {
    // Additional fields can be added as needed
}
