package tictactoe.mapper;

import tictactoe.domain.model.users.User;
import tictactoe.web.dto.auth.JwtRequest;
import tictactoe.web.dto.auth.JwtResponse;

public class JwtMapper {
    public static User fromRequestToUser(JwtRequest credentials) {
        return new User(credentials.getLogin());
    }

    public static JwtResponse toJwtResponse(String access, String refresh) {
        return new JwtResponse("Bearer", access, refresh);
    }
}
