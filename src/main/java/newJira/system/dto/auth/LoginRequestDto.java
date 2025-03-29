package newJira.system.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotEmpty(message = "Email is required")
    @Email(message = "email should be valid")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;
}