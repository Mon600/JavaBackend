package tictactoe.web.model.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tictactoe.domain.model.users.UserRole;

import java.util.HashSet;

@Getter
@AllArgsConstructor
public class BaseUserInfo {
    private String login;
    private HashSet<String> roles;
}
