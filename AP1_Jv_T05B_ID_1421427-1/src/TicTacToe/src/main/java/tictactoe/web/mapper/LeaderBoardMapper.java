package tictactoe.web.mapper;

import tictactoe.domain.model.users.PlayerStat;
import tictactoe.web.model.stat.LeaderBoardResponse;

public class LeaderBoardMapper {
    public static LeaderBoardResponse toResponse(PlayerStat playerStat) {
        return new LeaderBoardResponse(
                playerStat.getPlayerId().toString(),
                playerStat.getLogin(),
                playerStat.getWinsCount(),
                playerStat.getWinsRatio());
    }
}
