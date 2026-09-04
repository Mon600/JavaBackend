package tictactoe.domain.service;

import tictactoe.datasource.mapper.UserMapper;
import tictactoe.datasource.model.UserEntity;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.model.User;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class UserService implements UserInterface{
    private UserRepository userRepository;

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

    @Override
    public String getLoginById(UUID id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        return user.get().getLogin();
    }

}
