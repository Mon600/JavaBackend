package tictactoe.web.dto.users;


import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import tictactoe.web.dto.game.WebDTO;

@Getter

public class UserResponse extends BaseUserInfo{
    private final List<WebDTO> games;

    public UserResponse(String login, HashSet<String> roles, List<WebDTO> games) {
        super(login, roles);
        this.games = games;
    }
}
