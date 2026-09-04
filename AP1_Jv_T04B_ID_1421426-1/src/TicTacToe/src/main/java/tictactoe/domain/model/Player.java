package tictactoe.domain.model;

import lombok.Getter;


import java.util.UUID;

@Getter
public class Player {
    private final UUID playerId;
    private final GameSign sign;

    public Player(UUID playerId, GameSign sign) {
        this.playerId = playerId;
        this.sign = sign;
    }
}
