package tictactoe.domain.model.users;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerStat {
    private final UUID playerId;
    private final String login;
    private final int winsCount;
    private final double winsRatio;
}
