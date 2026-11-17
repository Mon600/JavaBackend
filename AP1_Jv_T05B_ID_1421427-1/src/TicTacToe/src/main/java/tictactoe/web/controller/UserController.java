package tictactoe.web.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import tictactoe.security.JwtAuthentication;
import tictactoe.domain.model.users.UserLoginRolePair;
import tictactoe.domain.service.game.player.Impl.GameServiceImpl;
import tictactoe.domain.service.auth.Impl.UserServiceImpl;
import tictactoe.mapper.UserInfoMapper;
import tictactoe.web.dto.users.UserInfo;
import tictactoe.web.dto.users.UserResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    private final GameServiceImpl gameServiceImpl;
    private final UserServiceImpl userServiceImpl;

    public UserController(GameServiceImpl gameServiceImpl, UserServiceImpl userServiceImpl) {
        this.gameServiceImpl = gameServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/{userId}")
    public UserResponse getUserInfo(@PathVariable("userId") UUID userId) {
        UserLoginRolePair userData = userServiceImpl.getLoginById(userId);
        String login = userData.login();
        return UserInfoMapper.toUserResponse(login, gameServiceImpl.getMyGames(userId), userData.roles());
    }

    @GetMapping("/get-me")
    public UserInfo getMeByToken(JwtAuthentication authentication) {
        UUID uuid = authentication.getPrincipal();
        return UserInfoMapper.toInfo(userServiceImpl.getUserByUUID(uuid));
    }
}
