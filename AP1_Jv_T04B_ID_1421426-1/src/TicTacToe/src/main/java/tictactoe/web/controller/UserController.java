package tictactoe.web.controller;


import org.springframework.web.bind.annotation.*;
import tictactoe.domain.model.Game;
import tictactoe.domain.service.GameService;
import tictactoe.domain.service.UserService;
import tictactoe.web.mapper.UserRequestMapper;
import tictactoe.web.model.UserResponse;
import tictactoe.web.model.WebDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final GameService gameService;
    private final UserService userService;

    public UserController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserResponse getUserInfo(@PathVariable("userId") UUID userId) {
        String login = userService.getLoginById(userId);
        List<Game> games = gameService.getMyGames(userId);
        return UserRequestMapper.toUserResponse(login, games);
    }
}
