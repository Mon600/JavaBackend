package tictactoe.domain.service.auth;

import java.util.Optional;
import java.util.UUID;
import tictactoe.datasource.model.UserEntity;
import tictactoe.domain.model.users.User;
import tictactoe.domain.model.users.UserLoginRolePair;

public interface UserInterface {
    boolean createUser(User user, String hashPassword);

    Optional<UserEntity> getUser(String login);

    UserLoginRolePair getLoginById(UUID id);
}
