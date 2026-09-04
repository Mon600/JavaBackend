package tictactoe.domain.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tictactoe.datasource.model.UserEntity;
import tictactoe.domain.model.User;
import tictactoe.web.mapper.UserRequestMapper;
import tictactoe.web.model.SignUpRequest;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    private String getHashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean signUpUser(SignUpRequest credentials) {
        try {
            User user = UserRequestMapper.toUser(credentials);
            String hashPassword = getHashPassword(credentials.getPassword());
            return userService.createUser(user, hashPassword);
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public UUID signInUser(String base64Credentials) {
        String[] credentials = decodeCredentials(base64Credentials);
        String login = credentials[0];
        String password = credentials[1];
        Optional<UserEntity> optionalEntity = userService.getUser(login);
        if (optionalEntity.isPresent() && passwordEncoder.matches(password, optionalEntity.get().getHashedPassword())) {
            return optionalEntity.get().getUserId();
        }
        throw new BadCredentialsException("Invalid login or password");
    }

    public static String[] decodeCredentials(String authHeader){
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new BadCredentialsException("Invalid credentials format");
        }
        String base64Credentials = authHeader.substring(6);
        byte[] decodedData = Base64.getDecoder().decode(base64Credentials);
        String data = new String(decodedData);
        String[] credentials = data.split(":");
        if (credentials.length != 2 || credentials[0].isBlank() || credentials[1].isBlank()) {
            throw new BadCredentialsException("Invalid credentials format");
        }
        return  credentials;
    }


}
