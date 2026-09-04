package tictactoe.web.model.users;


import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tictactoe.domain.model.users.UserRole;
import tictactoe.web.model.game.WebDTO;

@Getter

public class UserResponse extends BaseUserInfo{
    private final List<WebDTO> games;

    public UserResponse(String login, HashSet<String> roles, List<WebDTO> games) {
        super(login, roles);
        this.games = games;
    }
}
