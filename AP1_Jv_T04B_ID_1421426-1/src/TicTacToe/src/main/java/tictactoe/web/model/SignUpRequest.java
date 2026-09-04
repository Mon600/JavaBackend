package tictactoe.web.model;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest {
    @Size(min = 2, max = 32, message = "Wrong login length (2 to 32 characters long)")
    @Pattern(regexp = "^\\S+$", message = "Spaces are not allowed")
    private String login;

    @Size(min = 8, max = 64, message = "Wrong password length (8 to 64 characters long)")
    @Pattern(regexp = "^\\S+$", message = "Spaces are not allowed")
    private String password;
}
