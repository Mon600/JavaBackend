package tictactoe.domain.model.game;

import java.util.UUID;

import tictactoe.domain.model.rules.GameSign;

public record Player(UUID playerId, GameSign sign) {
}
