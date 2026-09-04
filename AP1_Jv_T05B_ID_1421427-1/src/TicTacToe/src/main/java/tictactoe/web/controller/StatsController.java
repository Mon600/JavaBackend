package tictactoe.web.controller;

import java.util.List;


import org.springframework.web.bind.annotation.*;
import tictactoe.domain.service.stat.LeaderBoardService;
import tictactoe.web.mapper.LeaderBoardMapper;
import tictactoe.web.model.stat.LeaderBoardResponse;

@RestController
@RequestMapping("/leaderboard")
public class StatsController {
    LeaderBoardService leaderBoardService;

    public StatsController(LeaderBoardService leaderBoardService) {
        this.leaderBoardService = leaderBoardService;
    }

    @GetMapping({"/", ""})
    public List<LeaderBoardResponse> getLeaderBoard(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return leaderBoardService.getNLeaders(limit).stream()
                .map(LeaderBoardMapper::toResponse)
                .toList();
    }
}
