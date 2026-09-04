package tictactoe.web.model;

import lombok.Builder;
import lombok.Getter;
import tictactoe.domain.model.*;

import java.util.ArrayList;
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
