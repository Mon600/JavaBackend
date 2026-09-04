package tictactoe.domain.service;

import tictactoe.datasource.model.UserEntity;
import tictactoe.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserInterface {
    boolean createUser(User user, String hashPassword);
    Optional<UserEntity> getUser(String login);
    String getLoginById(UUID id);
}
