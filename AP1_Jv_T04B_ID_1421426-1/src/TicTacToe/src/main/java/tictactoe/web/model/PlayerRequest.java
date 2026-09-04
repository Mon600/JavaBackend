package tictactoe.web.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tictactoe.domain.model.GameSign;
import tictactoe.domain.model.Player;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequest {
    private String playerId;
    private GameSign playerSign;
}
