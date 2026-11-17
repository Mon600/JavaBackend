package tictactoe.mapper;

import tictactoe.datasource.entity.UserEntity;
import tictactoe.domain.model.User;

public class UserMapper {
    public static UserEntity toEntity(User user, String hashPassword) {
        UserEntity entity = new UserEntity();
        entity.setLogin(user.getLogin());
        entity.setHashedPassword(hashPassword);
        return entity;
    }

    public static User toUser(UserEntity entity) {
        User user = new User();
        user.setUserId(entity.getUserId());
        user.setLogin(entity.getLogin());
        return user;
    }
}
