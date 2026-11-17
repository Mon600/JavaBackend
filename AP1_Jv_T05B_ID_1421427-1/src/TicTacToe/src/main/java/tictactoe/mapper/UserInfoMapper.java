package tictactoe.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import tictactoe.domain.model.game.Game;
import tictactoe.domain.model.users.User;
import tictactoe.domain.model.users.UserRole;
import tictactoe.web.dto.users.UserInfo;
import tictactoe.web.dto.users.UserResponse;
import tictactoe.web.dto.game.WebDTO;

public class UserInfoMapper {
    public static UserResponse toUserResponse(String login, List<Game> games, List<UserRole> role) {
        List<WebDTO> gamesResponse = games.stream().map(WebDTOMapper::toResponse).toList();
        return new UserResponse(
                login,
                new HashSet<>(
                        role.stream()
                                .map(Enum::toString)
                                .collect(Collectors.toSet())),
                gamesResponse
        );
    }

    public static UserInfo toInfo(User user) {
        return new UserInfo(
                user.getLogin(),
                user.getUserId().orElseThrow().toString(),
                user.getRoles().stream()
                        .map(Enum::toString)
                        .collect(Collectors.toCollection(HashSet::new)
        ));
    }
}
