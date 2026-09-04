package tictactoe.domain.service;

import lombok.Getter;
import tictactoe.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class AiService {
    private final int emptyCell = 0;

    public Turn calculateBestTurn(Game game) {
        int bestScore = Integer.MIN_VALUE;
        Field field = game.getField();
        List<Turn> bestTurns = new ArrayList<>();

        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getCols(); j++) {

                FieldPosition currentPosition = new FieldPosition(i, j);
                if (field.getFieldValue(new FieldPosition(i, j)) == emptyCell) {

                    Game newGame = game.simulateTurn(new Turn(currentPosition, game.getTurnOrder()));
                    Turn possibleTurn = newGame.getTurns().getLast();
                    int rating = minMax(newGame, 0);

                    if (rating > bestScore) {
                        bestScore = rating;
                        bestTurns.clear();
                        bestTurns.add(possibleTurn);

                    } else if (rating == bestScore) {
                        bestTurns.add(possibleTurn);
                    }
                }
            }
        }
        if (bestTurns.isEmpty()) {
            throw new IllegalStateException("No moves available");
        }
        return bestTurns.get(ThreadLocalRandom.current().nextInt(bestTurns.size()));
    }

    private int minMax(Game game, int depth) {
        GameState state = GameRules.checkGameEnd(game);
        Field field = game.getField();
        switch (state) {
            case AI_WIN -> {
                return 10 - depth;
            }
            case PLAYER_WIN -> {
                return  -10 + depth;
            }
            case DRAW -> {
                return 0;
            }
            case PLAYER_TURN -> {
                Player turningPlayer = game.getTurnOrder();
                boolean isAiTurn = turningPlayer.getPlayerId().equals(Constants.aiId);
                int bestScore = isAiTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

                for (int i = 0; i < field.getRows(); i++) {
                    for (int j = 0; j < field.getCols(); j++) {
                        FieldPosition currPos = new FieldPosition(i, j);
                        if (field.getFieldValue(currPos) == emptyCell) {
                            Game simulatedGame = game.simulateTurn(new Turn(currPos, game.getTurnOrder()));
                            int rating = minMax(simulatedGame, depth + 1);
                            bestScore = isAiTurn ? Math.max(rating, bestScore) : Math.min(rating, bestScore);
                        }
                    }
                }
                return bestScore;
            }
        }
        throw new IllegalStateException("Minimax reached unreachable state for game: " + game.getGameId());
    }

}
