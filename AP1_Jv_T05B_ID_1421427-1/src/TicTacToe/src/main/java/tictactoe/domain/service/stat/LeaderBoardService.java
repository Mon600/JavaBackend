package tictactoe.domain.service.stat;

import java.util.List;
import tictactoe.datasource.mapper.StatMapper;
import tictactoe.datasource.repository.StatRepository;
import tictactoe.domain.model.users.PlayerStat;

public class LeaderBoardService {
    private StatRepository statRepository;

    public LeaderBoardService(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public List<PlayerStat> getNLeaders(int leadersCnt) {
        return statRepository.getLeaders(leadersCnt).stream().map(StatMapper::toPlayStat).toList();
    }
}
