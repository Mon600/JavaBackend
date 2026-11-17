package tictactoe.web.dto.users;

import lombok.Getter;

import java.util.HashSet;


@Getter
public class UserInfo extends BaseUserInfo{
    private String id;

    public UserInfo(String login, String id, HashSet<String> roles) {
        super(login, roles);
        this.id = id;
    }
}
