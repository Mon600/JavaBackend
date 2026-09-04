package tictactoe.web.model.stat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaderBoardResponse {
    private String playerId;
    private String login;
    private int winsCnt;
    private double winsRatio;
}
