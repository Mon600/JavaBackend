package tictactoe.domain.model;



import java.util.List;

public class GameRules {
    private static final int EMPTY_CELL = 0;

    public static GameState checkGameEnd(Game game) {
        Field field = game.getField();
        List<Turn> turns = game.getTurns();
        GameState state = game.getState();
        if ((state != GameState.PLAYER_TURN && state != GameState.WAITING) || turns.isEmpty()) {
            return state;
        }

        Turn turn = turns.getLast();

        FieldPosition turnPosition = turn.getPosition();
        boolean resHorizontal = checkHorizontal(field, turnPosition);
        if (resHorizontal) {
            return game.getTurns().getLast().getPlayer().getPlayerId().equals(Constants.aiId) ?
                    GameState.AI_WIN :
                    GameState.PLAYER_WIN;
        }
        boolean resVertical = checkVertical(field, turnPosition);
        if (resVertical) {
            return game.getTurns().getLast().getPlayer().getPlayerId().equals(Constants.aiId) ?
                    GameState.AI_WIN :
                    GameState.PLAYER_WIN;
        }
        if ((turnPosition.getCol() == turnPosition.getRow())
                || ((turnPosition.getRow() + turnPosition.getCol()) == (field.getRows() - 1))) {
            if (checkDiagonals(field, turnPosition)) {
                return game.getTurns().getLast().getPlayer().getPlayerId().equals(Constants.aiId) ?
                        GameState.AI_WIN :
                        GameState.PLAYER_WIN;
            }
        }
        if (turns.size() == (field.getRows() * field.getCols())) {
            return GameState.DRAW;
        }
        return GameState.PLAYER_TURN;
    }

    public static boolean isCorrectTurn(Game game, Turn turn) {
        List<Turn> turns = game.getTurns();
        if (turns.isEmpty()) {
            return turn.getPlayer().getSign() == GameSign.X;
        } else {
            Player lastPlayer = turns.getLast().getPlayer();
            return (lastPlayer.getSign() != turn.getPlayer().getSign());
        }
    }

    public static boolean isValidMove(Field field, Turn turn) {
        FieldPosition position = turn.getPosition();
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
        int startMainDiagonalValue = field.getFieldValue(new FieldPosition(0,0));
        int startSecondaryDiagonalValue = field.getFieldValue(new FieldPosition(0, field.getCols() - 1));

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
                if (startSecondaryDiagonalValue != field.getFieldValue(new FieldPosition(j, field.getRows() - 1 - j))) {
                    resSecondary = false;
                    break;
                }
            }
        }

        return (resMain || resSecondary);
    }
}
