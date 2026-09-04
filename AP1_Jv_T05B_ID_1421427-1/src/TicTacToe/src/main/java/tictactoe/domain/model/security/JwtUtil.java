package tictactoe.domain.model.security;

import io.jsonwebtoken.Claims;
import tictactoe.domain.model.users.UserRole;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtil {
    public static JwtAuthentication createJwtAuthenticated(Claims claims) {
        if (!claims.containsKey("user_id") || !claims.containsKey("roles")) {
            throw new NoSuchElementException("No needed claims");
        }
        UUID userId = UUID.fromString(claims.get("user_id").toString());
        @SuppressWarnings("unchecked")
        List<String> rolesList = (List<String>) claims.get("roles");
        HashSet<UserRole> roles = rolesList.stream().map(UserRole::valueOf).collect(Collectors.toCollection(HashSet::new));
        return new JwtAuthentication(userId, roles);
    }
}
