package tictactoe.domain.model.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import tictactoe.domain.model.users.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;


public class JwtProvider {
    @Value("${SECRET_KEY_JWT}")
    private String key;

        public String createAccessToken(User user) {
            return Jwts.builder()
                    .header().type("JWT").and()
                    .subject(user.getLogin())
                    .claim("type", "access")
                    .claim("user_id", user.getUserId().orElseThrow().toString())
                    .claim("roles", user.getRoles().stream().toList())
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes()))
                    .compact();

        }

        public String createRefreshToken(User user) {
            return Jwts.builder()
                    .header().type("JWT").and()
                    .subject(user.getLogin())
                    .claim("type", "refresh")
                    .claim("user_id", user.getUserId().orElseThrow().toString())
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes()))
                    .compact();
        }


        public boolean isValidAccessToken(String token) {
            return isValidToken(token, TokenType.ACCESS);
        }

        public boolean isValidRefreshToken(String token) {
            return isValidToken(token, TokenType.REFRESH);
        }

        public boolean isValidToken(String token, TokenType tokenType) {
            try {
                Claims claims =  Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(key.getBytes()))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                String type = (String) claims.get("type");
                return tokenType.getType().equals(type);
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        }

        public Claims getClaims(String token) {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(key.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        }
}
