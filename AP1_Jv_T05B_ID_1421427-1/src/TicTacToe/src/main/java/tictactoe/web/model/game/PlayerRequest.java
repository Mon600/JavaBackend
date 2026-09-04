package tictactoe.web.model.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tictactoe.domain.model.game.GameSign;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequest {
    private String playerId;
    private GameSign playerSign;
}
