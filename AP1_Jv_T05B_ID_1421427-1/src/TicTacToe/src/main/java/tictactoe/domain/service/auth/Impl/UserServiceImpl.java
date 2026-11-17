package tictactoe.domain.service.auth.Impl;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import tictactoe.domain.service.auth.UserService;
import tictactoe.mapper.UserMapper;
import tictactoe.datasource.entity.UserEntity;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.model.users.User;
import tictactoe.domain.model.users.UserLoginRolePair;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
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
