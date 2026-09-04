package tictactoe.web.model.game;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WebDTO {
    private final UUID id;
    private String[][] field;
    private String currentPlayer;
    private List<TurnRequest> turns;
    private String state;
    private LocalDateTime created_at;
    private PlayerRequest player1;
    private PlayerRequest player2;
}
