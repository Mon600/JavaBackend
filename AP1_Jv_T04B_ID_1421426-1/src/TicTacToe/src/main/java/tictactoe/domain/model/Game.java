package tictactoe.domain.model;


import tictactoe.domain.rules.GameRules;
import tictactoe.domain.rules.GameSign;
import tictactoe.domain.rules.GameState;

import java.util.*;


public record Game(UUID gameId, Field field, List<Turn> turns, GameState state, Player player1, Player player2) {
    public Game(Player player1, Player player2, GameState state) {
        this(null, new Field(), new ArrayList<>(), state, player1, player2);
    }

    public Game(UUID gameId, Field field, List<Turn> turns, GameState state, Player player1, Player player2) {
        this.gameId = gameId;
        this.field = field;
        this.turns = turns != null ? new ArrayList<>(turns) : new ArrayList<>();
        this.state = state;
        this.player1 = player1;
        this.player2 = player2;

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
            GameSign lastTurnSign = lastTurn.getPlayer().sign();
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
        newField.setFieldValue(turn.getPosition(), (turn.getPlayer().playerId().equals(player1.playerId())) ? 1 : 2);
        return new Game(
                this.gameId,
                newField,
                turns,
                this.state,
                this.player1,
                this.player2
        );
    }

    public String getWinner() {
        if (this.state != GameState.AI_WIN && this.state != GameState.PLAYER_WIN) {
            throw new IllegalStateException("Game is still going on.");
        }
        UUID winnerId = this.turns.getLast().getPlayer().playerId();
        if (winnerId.equals(Constants.aiId)) {
            return GameState.AI_WIN.getStateString();
        }
        return GameState.PLAYER_WIN.getStateString() + String.format(" with UUID: %s", winnerId.toString());
    }
}