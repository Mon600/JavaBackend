package tictactoe.domain.service.Impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tictactoe.mapper.UserMapper;
import tictactoe.datasource.entity.UserEntity;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.model.User;
import tictactoe.domain.service.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
