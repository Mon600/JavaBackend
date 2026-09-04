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
import tictactoe.domain.model.AuthFilter;
import tictactoe.domain.service.AiService;
import tictactoe.domain.service.AuthService;
import tictactoe.domain.service.GameService;
import tictactoe.datasource.service.GameRepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tictactoe.domain.service.UserService;
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
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }

    @Bean
    public GameService gameService(GameRepositoryService repositoryService, AiService aiService, UserService userService) {
        return new GameService(repositoryService, aiService, userService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthService authService(PasswordEncoder passwordEncoder, UserService userService) {
        return new AuthService(passwordEncoder, userService);
    }

    @Bean
    public AuthFilter authFilter(PasswordEncoder passwordEncoder, AuthService authService) {
        return new AuthFilter(authService, passwordEncoder);
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
