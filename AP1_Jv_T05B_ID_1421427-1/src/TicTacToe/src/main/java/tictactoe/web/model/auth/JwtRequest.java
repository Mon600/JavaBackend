package tictactoe.web.model.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    @Size(min = 2, max = 32, message = "Wrong login length (2 to 32 characters long)")
    @Pattern(regexp = "^\\S+$", message = "Spaces are not allowed")
    private String login;

    @Size(min = 8, max = 64, message = "Wrong password length (8 to 64 characters long)")
    @Pattern(regexp = "^\\S+$", message = "Spaces are not allowed")
    private String password;
}
