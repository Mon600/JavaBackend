package tictactoe.web.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tictactoe.domain.model.GameSign;
import tictactoe.domain.model.Player;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TurnRequest {
    @Min(1) @Max(3) private int row;
    @Min(1) @Max(3) private int col;

}
