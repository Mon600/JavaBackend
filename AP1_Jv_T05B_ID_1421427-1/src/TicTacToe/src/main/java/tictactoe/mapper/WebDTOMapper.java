package tictactoe.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tictactoe.domain.model.game.*;
import tictactoe.domain.model.rules.GameSign;
import tictactoe.domain.model.rules.GameState;
import tictactoe.security.FieldPosition;
import tictactoe.web.dto.game.TurnRequest;
import tictactoe.web.dto.game.WebDTO;

public class WebDTOMapper {
    public static WebDTO toResponse(Game game) {
        return WebDTO.builder()
                .id(game.gameId())
                .field(getGameFieldResponse(game))
                .turns(getTurnsResponse(game))
                .currentPlayer(getCurrentPlayerResponse(game))
                .state(getStateResponse(game))
                .created_at(game.createdAt())
                .player1(PlayerRequestDTO.toPlayerRequest(game.player1()))
                .player2(PlayerRequestDTO.toPlayerRequest(game.player2()))
                .build();
    }

    public static String[][] getGameFieldResponse(Game game) {
        Field field = game.field();
        int rows = field.getRows();
        int cols = field.getCols();
        int[][] fieldMatrix = field.getFieldMatrix();
        String[][] fieldResponse = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int fieldCellValue = fieldMatrix[i][j];
                switch (fieldCellValue) {
                    case 0 -> fieldResponse[i][j] = " ";
                    case 1 -> fieldResponse[i][j] = game.player1().sign().getSignString();
                    case 2 -> fieldResponse[i][j] = game.player2().sign().getSignString();
                    default -> throw new IllegalArgumentException("Incorrect field value");
                }
            }
        }
        return fieldResponse;
    }

    public static List<TurnRequest> getTurnsResponse(Game game) {
        List<Turn> gameTurns = game.turns();
        List<TurnRequest> turnsResponse = new ArrayList<>();
        for (Turn turn : gameTurns) {
            int row = turn.position().getRow() + 1;
            int col = turn.position().getCol() + 1;
            turnsResponse.add(new TurnRequest(row, col));
        }
        return turnsResponse;
    }

    public static String getCurrentPlayerResponse(Game game) {
        Player turningPlayer = game.getTurnOrder();
        if (turningPlayer.equals(game.player1())) {
            return "Now turning player 1...";
        } else {
            return "Now turning player2...";
        }
    }

    public static String getStateResponse(Game game) {
        GameState state = game.state();
        if (state == GameState.AI_WIN || state == GameState.PLAYER_WIN) {
            return game.state().getStateString() + " " + game.getWinner().toString();
        }
        return state.getStateString();
    }

    public static Turn getTurnGame(TurnRequest turnInfo, UUID playerId, GameSign sign) {
        int row = turnInfo.getRow() - 1;
        int col = turnInfo.getCol() - 1;
        Player player = new Player(playerId, sign);
        return new Turn(new FieldPosition(row, col), player);
    }
}
