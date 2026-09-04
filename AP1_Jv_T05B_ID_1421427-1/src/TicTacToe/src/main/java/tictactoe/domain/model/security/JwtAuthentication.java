package tictactoe.domain.model.security;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.security.core.Authentication;
import tictactoe.domain.model.users.UserRole;

import java.util.HashSet;
import java.util.UUID;


public class JwtAuthentication implements Authentication {
    private boolean authenticated = true;
    private final UUID userId;
    private final HashSet<UserRole> roles;


    public JwtAuthentication(UUID userId, HashSet<UserRole> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    @NullUnmarked
    public HashSet<UserRole> getAuthorities() {
        return roles;
    }

    @Override
    public UUID getPrincipal() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException{
        this.authenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return null;
    }

}
