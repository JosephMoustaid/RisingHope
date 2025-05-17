package spring.charityapp.dto;

public record ResetPasswordRequestDTO(
        String email,
        String oldPassword,
        String newPassword
) {
}
