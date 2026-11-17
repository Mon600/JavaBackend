package tictactoe.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;


@Builder
@Getter
public class WebDTO {
    private final UUID id;
    private String[][] field;
    private String currentPlayer;
    private List<TurnRequest> turns;
    private String state;
    private PlayerRequest player1;
    private PlayerRequest player2;
}
