package tictactoe.datasource.mapper;

import java.math.BigDecimal;
import java.util.UUID;
import tictactoe.domain.model.users.PlayerStat;

public class StatMapper {
    public static PlayerStat toPlayStat(Object[] row) {
        UUID playerId = (UUID) row[0];
        String login = (String) row[1];
        Long wins = (Long) row[2];
        BigDecimal winsRatio = (BigDecimal) row[3];
        return new PlayerStat(playerId, login, wins.intValue(), winsRatio.doubleValue());
    }
}
