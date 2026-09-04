package tictactoe.datasource.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import tictactoe.datasource.model.GameEntity;
import tictactoe.datasource.model.TurnEntity;
import tictactoe.domain.model.game.Field;
import tictactoe.domain.model.game.Game;
import tictactoe.domain.model.game.Player;
import tictactoe.domain.model.game.Turn;
import tictactoe.domain.model.security.FieldPosition;

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
        if (game.getGameId() != null) {
            entity.setGameId(game.getGameId());
        }
        if (game.getCreatedAt() != null) {
            entity.setCreatedAt(game.getCreatedAt());
        }
        entity.setState(game.getState());
        entity.setTurnsHistory(getTurnsFromGame(game, entity));
        entity.setPlayer1Id(game.getPlayer1().getPlayerId());
        entity.setPlayer2Id(game.getPlayer2().getPlayerId());
        entity.setPlayer1sign(game.getPlayer1().getSign());
        entity.setPlayer2sign(game.getPlayer2().getSign());
        entity.setWinnerId(game.getWinnerId());
        return entity;
    }

    public static void updateEntity(GameEntity entity, Game game) {
        entity.setState(game.getState());
        entity.setWinnerId(game.getWinnerId());
        List<TurnEntity> existingTurns = entity.getTurnsHistory();
        List<Turn> domainTurns = game.getTurns();

        if (existingTurns.size() < domainTurns.size()) {
            for (int i = existingTurns.size(); i < domainTurns.size(); i++) {
                Turn domainTurn = domainTurns.get(i);
                TurnEntity newTurn =
                        TurnEntity.builder()
                                .row(domainTurn.getPosition().getRow())
                                .col(domainTurn.getPosition().getCol())
                                .playerId(domainTurn.getPlayer().getPlayerId())
                                .sign(domainTurn.getPlayer().getSign())
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
        for (Turn turn : game.getTurns()) {
            TurnEntity turnEntity =
                    TurnEntity.builder()
                            .row(turn.getPosition().getRow())
                            .col(turn.getPosition().getCol())
                            .playerId(turn.getPlayer().getPlayerId())
                            .sign(turn.getPlayer().getSign())
                            .game(entity)
                            .build();
            turnEntities.add(turnEntity);
        }

        return turnEntities;
    }
}
