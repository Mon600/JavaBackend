package tictactoe.domain.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tictactoe.domain.model.security.FieldPosition;

@AllArgsConstructor
@Getter
public class Turn {
    private FieldPosition position;
    private Player player;
}
