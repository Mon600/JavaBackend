package tictactoe.domain.model.users;

import java.util.List;

public record UserLoginRolePair(String login, List<UserRole> roles) {}
