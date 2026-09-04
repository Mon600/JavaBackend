package tictactoe.domain.model.game;

import java.util.UUID;
import lombok.Getter;

@Getter
public class Player {
    private final UUID playerId;
    private final GameSign sign;

    public Player(UUID playerId, GameSign sign) {
        this.playerId = playerId;
        this.sign = sign;
    }
}
