package tictactoe.web.mapper;

import tictactoe.domain.model.*;
import tictactoe.web.model.PlayerRequest;
import tictactoe.web.model.TurnRequest;
import tictactoe.web.model.WebDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class WebDTOMapper {
    public static WebDTO toResponse(Game game) {
        return WebDTO.builder()
                .id(game.getGameId())
                .field(getGameFieldResponse(game))
                .turns(getTurnsResponse(game))
                .currentPlayer(getCurrentPlayerResponse(game))
                .state(getStateResponse(game))
                .player1(PlayerRequestDTO.toPlayerRequest(game.getPlayer1()))
                .player2(PlayerRequestDTO.toPlayerRequest(game.getPlayer2()))
                .build();
    }

    public static String[][] getGameFieldResponse(Game game) {
        Field field = game.getField();
        int rows = field.getRows();
        int cols = field.getCols();
        int[][] fieldMatrix = field.getFieldMatrix();
        String[][] fieldResponse = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int fieldCellValue = fieldMatrix[i][j];
                switch (fieldCellValue) {
                    case 0 -> fieldResponse[i][j] = " ";
                    case 1 -> fieldResponse[i][j] = game.getPlayer1().getSign().getSignString();
                    case 2 -> fieldResponse[i][j] = game.getPlayer2().getSign().getSignString();
                    default -> throw new IllegalArgumentException("Incorrect field value");
                }
            }
        }
        return fieldResponse;
    }

    public static List<TurnRequest> getTurnsResponse(Game game) {
        List<Turn> gameTurns = game.getTurns();
        List<TurnRequest> turnsResponse = new ArrayList<>();
        for (Turn turn : gameTurns) {
            int row = turn.getPosition().getRow() + 1;
            int col = turn.getPosition().getCol() + 1;
            turnsResponse.add(new TurnRequest(row, col));
        }
        return turnsResponse;
    }

    public static String getCurrentPlayerResponse(Game game) {
        Player turningPlayer = game.getTurnOrder();
        if (turningPlayer == game.getPlayer1()) {
            return "Now turning player 1...";
        } else {
            return "Now turning player2...";
        }
    }

    public static String getStateResponse(Game game) {
        GameState state = game.getState();
        if (state == GameState.AI_WIN || state == GameState.PLAYER_WIN) {
            return game.getWinner();
        }
        return state.getStateString();
    }

    public static Turn getTurnGame(TurnRequest turnInfo, UUID playerId, GameSign sign) {
        int row = turnInfo.getRow() - 1;
        int col = turnInfo.getCol() - 1;
        Player player = new Player(playerId, sign);
        return new Turn(
                new FieldPosition(row, col),
                player
        );
    }
}
