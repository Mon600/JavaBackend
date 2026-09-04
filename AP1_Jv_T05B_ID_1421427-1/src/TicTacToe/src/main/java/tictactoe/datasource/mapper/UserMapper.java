package tictactoe.datasource.mapper;

import tictactoe.datasource.model.UserEntity;
import tictactoe.domain.model.users.User;
import tictactoe.domain.model.users.UserRole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserEntity toEntity(User user, String hashPassword) {
        UserEntity entity = new UserEntity();
        entity.setLogin(user.getLogin());
        entity.setHashedPassword(hashPassword);
        entity.setRoles(new HashSet<>(user.getRoles()));
        return entity;
    }

    public static User toUser(UserEntity entity) {
        User user = new User();
        user.setUserId(entity.getUserId());
        user.setLogin(entity.getLogin());
        List<UserRole> roles = new ArrayList<>(entity.getRoles());
        user.setRoles(roles);
        return user;
    }
}
