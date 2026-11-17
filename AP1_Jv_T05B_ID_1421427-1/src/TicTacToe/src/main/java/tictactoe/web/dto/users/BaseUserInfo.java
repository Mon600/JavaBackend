package tictactoe.web.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;

@Getter
@AllArgsConstructor
public class BaseUserInfo {
    private String login;
    private HashSet<String> roles;
}
