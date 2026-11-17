package tictactoe.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
@Component
public class AuthFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;


    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length());
        }
        return null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest mappedRequest = (HttpServletRequest) request;
        HttpServletResponse mappedResponse = (HttpServletResponse) response;
        String token = getTokenFromHeader(mappedRequest);
        if (token != null && !token.isEmpty()) {
            if (jwtProvider.isValidAccessToken(token)) {
                Claims claims = jwtProvider.getClaims(token);
                JwtAuthentication authentication = JwtUtil.createJwtAuthenticated(claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                mappedResponse.sendError(401, "Invalid token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
