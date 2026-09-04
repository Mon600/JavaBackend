package tictactoe.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class User {
    private UUID userId;
    private String login;

    public User(String login) {
        this.userId = null;
        this.login = login;
    }


    public Optional<UUID> getUserId() {
        return Optional.ofNullable(this.userId);
    }
}
