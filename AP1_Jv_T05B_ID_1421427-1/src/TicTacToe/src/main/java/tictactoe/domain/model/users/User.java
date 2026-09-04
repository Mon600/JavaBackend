package tictactoe.domain.model.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User {
    private UUID userId;
    private String login;
    private List<UserRole> roles = new ArrayList<>();

    public User(String login) {
        this.userId = null;
        this.login = login;
        this.roles.add(UserRole.USER);
    }

    public Optional<UUID> getUserId() {
        return Optional.ofNullable(this.userId);
    }
}
