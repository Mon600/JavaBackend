package tictactoe.domain.model.game;

import java.time.LocalDateTime;
import java.util.*;
import lombok.Getter;

@Getter
public class Game {
    private final UUID gameId;
    private final Field field;
    private final List<Turn> turns;
    private final GameState state;
    private final LocalDateTime createdAt;
    private final Player player1;
    private final Player player2;
    private final UUID winnerId;

    public Game(Player player1, Player player2, GameState state) {
        this.gameId = null;
        this.field = new Field();
        this.turns = new ArrayList<>();
        this.state = state;
        this.createdAt = null;
        this.player1 = player1;
        this.player2 = player2;
        this.winnerId = null;
    }

    public Game(
            UUID gameId,
            Field field,
            List<Turn> turns,
            GameState state,
            LocalDateTime createdAt,
            Player player1,
            Player player2,
            UUID winnerId) {
        this.gameId = gameId;
        this.field = field;
        this.turns = turns != null ? new ArrayList<>(turns) : new ArrayList<>();
        this.state = state;
        this.player1 = player1;
        this.player2 = player2;
        this.createdAt = createdAt;
        this.winnerId = winnerId;
    }

    public List<Turn> getTurns() {
        return Collections.unmodifiableList(turns);
    }

    public Player getTurnOrder() {
        if (turns.isEmpty()) {
            return (player1.getSign().equals(GameSign.X)) ? player1 : player2;
        } else {
            Turn lastTurn = turns.getLast();
            GameSign lastTurnSign = lastTurn.getPlayer().getSign();
            return (lastTurnSign.equals(player1.getSign())) ? player2 : player1;
        }
    }

    public Game simulateTurn(Turn turn) {
        boolean isCorrectTurn = GameRules.isCorrectTurn(this, turn);
        if (!isCorrectTurn) {
            throw new IllegalStateException("Wrong player's turn.");
        }
        if (!GameRules.isValidMove(this.field, turn)) {
            throw new IllegalStateException("This cell is not empty!");
        }
        Field newField = Field.getFieldCopy(this.field);
        List<Turn> turns = new ArrayList<>(this.turns);
        turns.add(turn);
        newField.setFieldValue(
                turn.getPosition(),
                (turn.getPlayer().getPlayerId().equals(player1.getPlayerId())) ? 1 : 2);
        return new Game(gameId, newField, turns, state, createdAt, player1, player2, winnerId);
    }

    public UUID getWinner() {
        if (this.state != GameState.AI_WIN && this.state != GameState.PLAYER_WIN) {
            throw new IllegalStateException("Game is still going on.");
        }
        return this.turns.getLast().getPlayer().getPlayerId();
    }
}
