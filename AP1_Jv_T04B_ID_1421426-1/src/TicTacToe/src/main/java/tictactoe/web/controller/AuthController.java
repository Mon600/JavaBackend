package tictactoe.web.controller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tictactoe.domain.service.AuthService;
import tictactoe.web.model.SignUpRequest;


import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest credentials) {
        boolean registrationStatus = authService.signUpUser(credentials);
        if (!registrationStatus) {
            return ResponseEntity.status(409).body("Registration failed!");
        }
        return ResponseEntity.status(201).body("Success!");
    }


    @PostMapping("login")
    public ResponseEntity<String> signIn(@RequestHeader("Authorization") String authHeader) throws AuthenticationException {
        return ResponseEntity.status(201).body(authService.signInUser(authHeader).toString());
    }
}
