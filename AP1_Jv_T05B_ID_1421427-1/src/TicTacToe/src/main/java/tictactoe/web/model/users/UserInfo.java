package tictactoe.web.model.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tictactoe.domain.model.users.UserRole;

import java.util.HashSet;
import java.util.UUID;


@Getter
public class UserInfo extends BaseUserInfo{
    private String id;

    public UserInfo(String login, String id, HashSet<String> roles) {
        super(login, roles);
        this.id = id;
    }
}
