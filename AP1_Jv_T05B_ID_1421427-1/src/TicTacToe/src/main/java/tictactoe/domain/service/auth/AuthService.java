package tictactoe.domain.service.auth;

import java.util.Optional;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tictactoe.domain.service.auth.Impl.UserServiceImpl;
import tictactoe.mapper.UserMapper;
import tictactoe.datasource.entity.UserEntity;
import tictactoe.security.JwtAuthentication;
import tictactoe.security.JwtProvider;
import tictactoe.security.JwtUtil;
import tictactoe.domain.model.users.User;
import tictactoe.mapper.JwtMapper;
import tictactoe.web.dto.auth.JwtRequest;
import tictactoe.web.dto.auth.JwtResponse;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userServiceImpl;
    private final JwtProvider jwtProvider;

    public AuthService(PasswordEncoder passwordEncoder, UserServiceImpl userServiceImpl, JwtProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userServiceImpl = userServiceImpl;
        this.jwtProvider = jwtProvider;
    }

    private String getHashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean signUpUser(JwtRequest credentials) {
        try {
            User user = JwtMapper.fromRequestToUser(credentials);
            String hashPassword = getHashPassword(credentials.getPassword());
            return userServiceImpl.createUser(user, hashPassword);
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public JwtResponse signInUser(JwtRequest credentials) {
        String login = credentials.getLogin();
        String password = credentials.getPassword();
        Optional<UserEntity> optionalEntity = userServiceImpl.getUser(login);
        if (optionalEntity.isPresent()
                && passwordEncoder.matches(password, optionalEntity.get().getHashedPassword())) {
            User user = UserMapper.toUser(optionalEntity.get());
            String accessToken = this.jwtProvider.createAccessToken(user);
            String refreshToken = this.jwtProvider.createRefreshToken(user);
            JwtAuthentication jwtAuthentication = createAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            return new JwtResponse("Bearer", accessToken, refreshToken);
        }
        throw new BadCredentialsException("Invalid login or password");
    }

    public User getUserByToken(String token) {
        Claims claims = jwtProvider.getClaims(token);
        UUID userId = UUID.fromString(claims.get("user_id").toString());
        Optional<UserEntity> userEntity = userServiceImpl.getUserEntityByUUID(userId);
        if (userEntity.isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }
        return UserMapper.toUser(userEntity.get());
    }

    private boolean validateRefreshToken(String token) {
        return (token != null && jwtProvider.isValidRefreshToken(token));
    }

    private boolean validateAccessToken(String token) {
        return (token != null && jwtProvider.isValidAccessToken(token));
    }

    public JwtResponse refreshAccess(String refresh) {
        if (!validateRefreshToken(refresh)) {
            throw new BadCredentialsException("Invalid token");
        }
        User user = getUserByToken(refresh);
        String newAccess = jwtProvider.createAccessToken(user);
        return new JwtResponse("Bearer", newAccess, refresh);
    }

    public JwtResponse refreshRefresh(String refresh) {
        if (!validateRefreshToken(refresh)) {
            throw new BadCredentialsException("Invalid token");
        }
        User user = getUserByToken(refresh);
        String newAccess = jwtProvider.createAccessToken(user);
        String newRefresh = jwtProvider.createRefreshToken(user);
        return new JwtResponse("Bearer", newAccess, newRefresh);

    }

    public JwtAuthentication createAuthentication(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new BadCredentialsException("Invalid token");
        }
        Claims claims = jwtProvider.getClaims(accessToken);
        return JwtUtil.createJwtAuthenticated(claims);
    }

}
