package tictactoe.domain.model.rules;

import tictactoe.domain.model.*;
import tictactoe.domain.model.game.*;
import tictactoe.security.FieldPosition;

import java.util.List;

public class GameRules {
    private static final int EMPTY_CELL = 0;

    public static GameState checkGameEnd(Game game) {
        Field field = game.field();
        List<Turn> turns = game.turns();
        GameState state = game.state();
        if ((state != GameState.PLAYER_TURN && state != GameState.WAITING) || turns.isEmpty()) {
            return state;
        }

        Turn turn = turns.getLast();

        FieldPosition turnPosition = turn.position();
        boolean resHorizontal = checkHorizontal(field, turnPosition);
        if (resHorizontal) {
            return game.turns().getLast().player().playerId().equals(Constants.aiId)
                    ? GameState.AI_WIN
                    : GameState.PLAYER_WIN;
        }
        boolean resVertical = checkVertical(field, turnPosition);
        if (resVertical) {
            return game.turns().getLast().player().playerId().equals(Constants.aiId)
                    ? GameState.AI_WIN
                    : GameState.PLAYER_WIN;
        }
        if ((turnPosition.getCol() == turnPosition.getRow())
                || ((turnPosition.getRow() + turnPosition.getCol()) == (field.getRows() - 1))) {
            if (checkDiagonals(field, turnPosition)) {
                return game.turns().getLast().player().playerId().equals(Constants.aiId)
                        ? GameState.AI_WIN
                        : GameState.PLAYER_WIN;
            }
        }
        if (turns.size() == (field.getRows() * field.getCols())) {
            return GameState.DRAW;
        }
        return GameState.PLAYER_TURN;
    }

    public static boolean isCorrectTurn(Game game, Turn turn) {
        Player currentTurnPlayer = game.getTurnOrder();
        return (currentTurnPlayer.playerId().equals(turn.player().playerId()));
    }

    public static boolean isValidMove(Field field, Turn turn) {
        FieldPosition position = turn.position();
        return (field.getFieldValue(position) == 0);
    }

    private static boolean checkHorizontal(Field field, FieldPosition position) {
        int row = position.getRow();
        int horizontalStartValue = field.getFieldValue(new FieldPosition(row, 0));

        if (horizontalStartValue == EMPTY_CELL) {
            return false;
        }
        for (int col = 1; col < field.getCols(); col++) {
            if (horizontalStartValue != field.getFieldValue(new FieldPosition(row, col))) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkVertical(Field field, FieldPosition position) {
        int col = position.getCol();
        int verticalStartValue = field.getFieldValue(new FieldPosition(0, col));

        if (verticalStartValue == EMPTY_CELL) {
            return false;
        }

        for (int row = 1; row < field.getRows(); row++) {
            if (verticalStartValue != field.getFieldValue(new FieldPosition(row, col))) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkDiagonals(Field field, FieldPosition position) {
        int startMainDiagonalValue = field.getFieldValue(new FieldPosition(0, 0));
        int startSecondaryDiagonalValue =
                field.getFieldValue(new FieldPosition(0, field.getCols() - 1));

        boolean resMain = true;
        boolean resSecondary = true;

        if (startMainDiagonalValue == EMPTY_CELL) {
            resMain = false;
        } else {
            for (int i = 1; i < field.getRows(); i++) {
                if (startMainDiagonalValue != field.getFieldValue(new FieldPosition(i, i))) {
                    resMain = false;
                    break;
                }
            }
        }
        if (startSecondaryDiagonalValue == EMPTY_CELL) {
            resSecondary = false;
        } else {
            for (int j = 1; j < field.getRows(); j++) {
                if (startSecondaryDiagonalValue
                        != field.getFieldValue(new FieldPosition(j, field.getRows() - 1 - j))) {
                    resSecondary = false;
                    break;
                }
            }
        }

        return (resMain || resSecondary);
    }
}
