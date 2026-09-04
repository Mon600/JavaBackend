package tictactoe.domain.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldPosition {
    private int row;
    private int col;
}
