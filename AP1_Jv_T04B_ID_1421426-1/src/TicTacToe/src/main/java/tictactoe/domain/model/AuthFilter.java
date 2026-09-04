package tictactoe.domain.model;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.GenericFilterBean;
import tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;


public class AuthFilter extends GenericFilterBean {
    AuthService authService;
    PasswordEncoder passwordEncoder;

    public AuthFilter(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest mappedRequest = (HttpServletRequest) request;
        HttpServletResponse mappedResponse = (HttpServletResponse) response;
        String path = mappedRequest.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(mappedRequest.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String headerAuthString = mappedRequest.getHeader("Authorization");
            UUID userId = authService.signInUser(headerAuthString);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        } catch (BadCredentialsException e) {
            mappedResponse.sendError(401, e.getMessage());
            return;
        }
    }
}
