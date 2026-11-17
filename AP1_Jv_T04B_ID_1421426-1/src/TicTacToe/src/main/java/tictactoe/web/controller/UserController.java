package tictactoe.web.controller;


import org.springframework.web.bind.annotation.*;
import tictactoe.domain.model.Game;
import tictactoe.domain.service.Impl.GameServiceImpl;
import tictactoe.domain.service.Impl.UserServiceImpl;
import tictactoe.mapper.UserRequestMapper;
import tictactoe.web.dto.UserResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final GameServiceImpl gameServiceImpl;
    private final UserServiceImpl userServiceImpl;

    public UserController(GameServiceImpl gameServiceImpl, UserServiceImpl userServiceImpl) {
        this.gameServiceImpl = gameServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/{userId}")
    public UserResponse getUserInfo(@PathVariable("userId") UUID userId) {
        String login = userServiceImpl.getLoginById(userId);
        List<Game> games = gameServiceImpl.getMyGames(userId);
        return UserRequestMapper.toUserResponse(login, games);
    }
}
