package tictactoe.domain.service.auth;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import tictactoe.datasource.mapper.UserMapper;
import tictactoe.datasource.model.UserEntity;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.model.users.User;
import tictactoe.domain.model.users.UserLoginRolePair;

public class UserService implements UserInterface {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean createUser(User user, String hashPassword) {
        UserEntity newUser = UserMapper.toEntity(user, hashPassword);
        userRepository.save(newUser);
        return true;
    }

    @Override
    public Optional<UserEntity> getUser(String login) {
        return userRepository.findByLogin(login);
    }


    public Optional<UserEntity> getUserEntityByUUID(UUID id) {
        return userRepository.findById(id);
    }

    public User getUserByUUID(UUID id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        return UserMapper.toUser(userEntity.orElseThrow());
    }

    @Override
    public UserLoginRolePair getLoginById(UUID id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        return new UserLoginRolePair(
                user.get().getLogin(), user.get().getRoles().stream().toList());
    }
}
