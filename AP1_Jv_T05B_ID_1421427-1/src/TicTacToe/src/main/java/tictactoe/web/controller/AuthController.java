package tictactoe.web.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import tictactoe.domain.service.auth.AuthService;
import tictactoe.web.model.auth.JwtRequest;
import tictactoe.web.model.auth.JwtResponse;
import tictactoe.web.model.auth.RefreshJwtRequest;



@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestBody JwtRequest credentials) {
        boolean registrationStatus = authService.signUpUser(credentials);
        if (!registrationStatus) {
            return ResponseEntity.status(409).body("Registration failed!");
        }
        return ResponseEntity.status(201).body("Success!");
    }

    @PostMapping("/auth/sign-in")
    public JwtResponse signIn(@Valid @RequestBody JwtRequest authBody) {
        return authService.signInUser(authBody);
    }


    @PostMapping("/token/refresh/access")
    public JwtResponse refreshAccessToken(@Valid @RequestBody RefreshJwtRequest authBody) {
        return authService.refreshAccess(authBody.getRefreshToken());
    }

    @PostMapping("/token/refresh/refresh")
    public JwtResponse refreshRefreshToken(@Valid @RequestBody RefreshJwtRequest authBody) {
        return authService.refreshRefresh(authBody.getRefreshToken());
    }
}
