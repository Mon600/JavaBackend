package tictactoe.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tictactoe.domain.rules.GameSign;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequest {
    private String playerId;
    private GameSign playerSign;
}
