package tictactoe.web.mapper;

import tictactoe.domain.model.Game;
import tictactoe.domain.model.User;
import tictactoe.web.model.SignUpRequest;
import tictactoe.web.model.UserResponse;
import tictactoe.web.model.WebDTO;

import java.util.List;

public class UserRequestMapper {
    public static User toUser(SignUpRequest credentials) {
        return new User(credentials.getLogin());
    }
    public static UserResponse toUserResponse(String login, List<Game> games) {
        List<WebDTO> gamesResponse = games.stream().map(WebDTOMapper::toResponse).toList();
        return new UserResponse(login, gamesResponse);
    }
}
