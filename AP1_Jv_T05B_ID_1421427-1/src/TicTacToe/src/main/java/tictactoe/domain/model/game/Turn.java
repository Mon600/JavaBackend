package tictactoe.domain.model.game;

import tictactoe.security.FieldPosition;

public record Turn(FieldPosition position, Player player) {
}
