package tictactoe.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Turn {
    private FieldPosition position;
    private Player player;
}
