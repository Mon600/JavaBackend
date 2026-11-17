package tictactoe.domain.model;

import tictactoe.domain.rules.GameSign;


import java.util.UUID;

public record Player(UUID playerId, GameSign sign) {
}
