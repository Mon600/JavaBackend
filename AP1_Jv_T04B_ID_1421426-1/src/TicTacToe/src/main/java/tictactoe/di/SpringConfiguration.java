package tictactoe.di;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.service.*;
import tictactoe.domain.service.Impl.GameServiceImpl;
import tictactoe.domain.service.Impl.UserServiceImpl;
import tictactoe.security.AuthFilter;
import tictactoe.datasource.service.GameRepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SpringConfiguration {

    @Bean
    public GameRepositoryService repositoryService(GameRepository gameRepository) {
        return new GameRepositoryService(gameRepository);
    }

    @Bean
    public AiService aiService() {
        return new AiService();
    }


    @Bean
    public UserServiceImpl userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public GameServiceImpl gameService(GameRepositoryService repositoryService, AiService aiService, UserServiceImpl userServiceImpl) {
        return new GameServiceImpl(repositoryService, aiService, userServiceImpl);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthService authService(PasswordEncoder passwordEncoder, UserServiceImpl userServiceImpl) {
        return new AuthService(passwordEncoder, userServiceImpl);
    }

    @Bean
    public AuthFilter authFilter(PasswordEncoder passwordEncoder, AuthService authService) {
        return new AuthFilter(authService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated())
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
