package tictactoe.domain.model.game;

import java.time.LocalDateTime;
import java.util.*;

import tictactoe.domain.model.rules.GameRules;
import tictactoe.domain.model.rules.GameSign;
import tictactoe.domain.model.rules.GameState;

public record Game(UUID gameId, Field field, List<Turn> turns, GameState state, LocalDateTime createdAt, Player player1,
                   Player player2, UUID winnerId) {
    public Game(Player player1, Player player2, GameState state) {
        this(null, new Field(), new ArrayList<>(), state, null, player1, player2, null);
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

    @Override
    public List<Turn> turns() {
        return Collections.unmodifiableList(turns);
    }

    public Player getTurnOrder() {
        if (turns.isEmpty()) {
            return (player1.sign().equals(GameSign.X)) ? player1 : player2;
        } else {
            Turn lastTurn = turns.getLast();
            GameSign lastTurnSign = lastTurn.player().sign();
            return (lastTurnSign.equals(player1.sign())) ? player2 : player1;
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
                turn.position(),
                (turn.player().playerId().equals(player1.playerId())) ? 1 : 2);
        return new Game(gameId, newField, turns, state, createdAt, player1, player2, winnerId);
    }

    public UUID getWinner() {
        if (this.state != GameState.AI_WIN && this.state != GameState.PLAYER_WIN) {
            throw new IllegalStateException("Game is still going on.");
        }
        return this.turns.getLast().player().playerId();
    }
}
