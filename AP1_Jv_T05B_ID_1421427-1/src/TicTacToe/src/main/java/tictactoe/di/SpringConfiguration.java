package tictactoe.di;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.datasource.repository.StatRepository;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.datasource.service.GameRepositoryService;
import tictactoe.domain.model.security.AuthFilter;
import tictactoe.domain.model.security.JwtProvider;
import tictactoe.domain.service.auth.AuthService;
import tictactoe.domain.service.auth.UserService;
import tictactoe.domain.service.game.ai.AiService;
import tictactoe.domain.service.game.player.GameService;
import tictactoe.domain.service.stat.LeaderBoardService;

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
    public GameService gameService(
            GameRepositoryService repositoryService, AiService aiService, UserService userService) {
        return new GameService(repositoryService, aiService, userService);
    }

    @Bean
    public LeaderBoardService leaderBoardService(StatRepository statRepository) {
        return new LeaderBoardService(statRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProvider jwtProvider() {return new JwtProvider(); }

    @Bean
    public AuthService authService(PasswordEncoder passwordEncoder, UserService userService, JwtProvider jwtProvider) {
        return new AuthService(passwordEncoder, userService, jwtProvider);
    }

    @Bean
    public AuthFilter authFilter(JwtProvider jwtProvider) {
        return new AuthFilter( jwtProvider);
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter)
            throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/auth/**",
                                                "/token/refresh/access",
                                                "/token/refresh/refresh")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
