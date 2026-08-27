package ro.utcluj.unihub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ro.utcluj.unihub.model.Role;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank @Size(max=80) String firstName, @NotBlank @Size(max=80) String lastName,
                                  @NotBlank @Email String email, @NotBlank @Size(min=8,max=100) String password) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record AuthResponse(String token, Long userId, String firstName, String lastName, String email, Role role) {}
}

