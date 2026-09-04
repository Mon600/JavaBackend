package tictactoe.web.mapper;

import tictactoe.domain.model.users.User;
import tictactoe.web.model.auth.JwtRequest;
import tictactoe.web.model.auth.JwtResponse;

public class JwtMapper {
    public static User fromRequestToUser(JwtRequest credentials) {
        return new User(credentials.getLogin());
    }

    public static JwtResponse toJwtResponse(String access, String refresh) {
        return new JwtResponse("Bearer", access, refresh);
    }
}
