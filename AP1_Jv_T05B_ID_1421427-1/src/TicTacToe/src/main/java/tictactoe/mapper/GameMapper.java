package tictactoe.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import tictactoe.datasource.entity.GameEntity;
import tictactoe.datasource.entity.TurnEntity;
import tictactoe.domain.model.game.Field;
import tictactoe.domain.model.game.Game;
import tictactoe.domain.model.game.Player;
import tictactoe.domain.model.game.Turn;
import tictactoe.security.FieldPosition;

public class GameMapper {

    public static Game toGame(GameEntity entity) {
        Player player1 = new Player(entity.getPlayer1Id(), entity.getPlayer1sign());
        Player player2 = new Player(entity.getPlayer2Id(), entity.getPlayer2sign());
        return new Game(
                entity.getGameId(),
                getFieldFromEntity(entity),
                getTurnsFromEntity(entity),
                entity.getState(),
                entity.getCreatedAt(),
                player1,
                player2,
                entity.getWinnerId());
    }

    public static GameEntity toEntity(Game game) {
        GameEntity entity = new GameEntity();
        if (game.gameId() != null) {
            entity.setGameId(game.gameId());
        }
        if (game.createdAt() != null) {
            entity.setCreatedAt(game.createdAt());
        }
        entity.setState(game.state());
        entity.setTurnsHistory(getTurnsFromGame(game, entity));
        entity.setPlayer1Id(game.player1().playerId());
        entity.setPlayer2Id(game.player2().playerId());
        entity.setPlayer1sign(game.player1().sign());
        entity.setPlayer2sign(game.player2().sign());
        entity.setWinnerId(game.winnerId());
        return entity;
    }

    public static void updateEntity(GameEntity entity, Game game) {
        entity.setState(game.state());
        entity.setWinnerId(game.winnerId());
        List<TurnEntity> existingTurns = entity.getTurnsHistory();
        List<Turn> domainTurns = game.turns();

        if (existingTurns.size() < domainTurns.size()) {
            for (int i = existingTurns.size(); i < domainTurns.size(); i++) {
                Turn domainTurn = domainTurns.get(i);
                TurnEntity newTurn =
                        TurnEntity.builder()
                                .row(domainTurn.position().getRow())
                                .col(domainTurn.position().getCol())
                                .playerId(domainTurn.player().playerId())
                                .sign(domainTurn.player().sign())
                                .game(entity)
                                .build();
                existingTurns.add(newTurn);
            }
        }
    }

    private static Field getFieldFromEntity(GameEntity entity) {
        Field gameField = new Field();
        List<TurnEntity> turns = entity.getTurnsHistory();
        UUID player1Id = entity.getPlayer1Id();
        UUID player2Id = entity.getPlayer2Id();

        for (TurnEntity turn : turns) {
            int col = turn.getCol();
            int row = turn.getRow();
            UUID playerId = turn.getPlayerId();

            int turnValue = 0;
            if (playerId.equals(player1Id)) {
                turnValue = 1;
            } else if (playerId.equals(player2Id)) {
                turnValue = 2;
            }

            gameField.setFieldValue(new FieldPosition(row, col), turnValue);
        }

        return gameField;
    }

    private static List<Turn> getTurnsFromEntity(GameEntity entity) {
        List<Turn> turns = new ArrayList<>();
        List<TurnEntity> turnsHistory = entity.getTurnsHistory();
        for (TurnEntity turn : turnsHistory) {
            FieldPosition pos = new FieldPosition(turn.getRow(), turn.getCol());
            Player player = new Player(turn.getPlayerId(), turn.getSign());
            turns.add(new Turn(pos, player));
        }
        return turns;
    }

    private static List<TurnEntity> getTurnsFromGame(Game game, GameEntity entity) {
        List<TurnEntity> turnEntities = new ArrayList<>();
        for (Turn turn : game.turns()) {
            TurnEntity turnEntity =
                    TurnEntity.builder()
                            .row(turn.position().getRow())
                            .col(turn.position().getCol())
                            .playerId(turn.player().playerId())
                            .sign(turn.player().sign())
                            .game(entity)
                            .build();
            turnEntities.add(turnEntity);
        }

        return turnEntities;
    }
}
