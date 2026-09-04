package tictactoe.web.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import tictactoe.domain.model.security.JwtAuthentication;
import tictactoe.domain.model.users.UserLoginRolePair;
import tictactoe.domain.service.game.player.GameService;
import tictactoe.domain.service.auth.UserService;
import tictactoe.web.mapper.UserInfoMapper;
import tictactoe.web.model.users.UserInfo;
import tictactoe.web.model.users.UserResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    private final GameService gameService;
    private final UserService userService;

    public UserController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserResponse getUserInfo(@PathVariable("userId") UUID userId) {
        UserLoginRolePair userData = userService.getLoginById(userId);
        String login = userData.login();
        return UserInfoMapper.toUserResponse(login, gameService.getMyGames(userId), userData.roles());
    }

    @GetMapping("/get-me")
    public UserInfo getMeByToken(JwtAuthentication authentication) {
        UUID uuid = authentication.getPrincipal();
        return UserInfoMapper.toInfo(userService.getUserByUUID(uuid));
    }
}
