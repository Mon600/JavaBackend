package tictactoe.mapper;

import tictactoe.datasource.entity.GameEntity;
import tictactoe.datasource.entity.TurnEntity;
import tictactoe.domain.model.*;

import java.util.ArrayList;
import java.util.List;

public class GameMapper {

    public static Game toGame(GameEntity entity) {
        Player player1 = new Player(entity.getPlayer1Id(), entity.getPlayer1sign());
        Player player2 = new Player(entity.getPlayer2Id(), entity.getPlayer2sign());
        return new Game(
                entity.getGameId(),
                getFieldFromEntity(entity),
                getTurnsFromEntity(entity),
                entity.getState(),
                player1,
                player2
        );
    }

    public static GameEntity toEntity(Game game) {
        GameEntity entity = new GameEntity();
        if (game.gameId() != null) {
            entity.setGameId(game.gameId());
        }
        entity.setFieldState(getFieldStateFromGame(game));
        entity.setState(game.state());
        entity.setTurnsHistory(getTurnsFromGame(game, entity));
        entity.setPlayer1Id(game.player1().playerId());
        entity.setPlayer2Id(game.player2().playerId());
        entity.setPlayer1sign(game.player1().sign());
        entity.setPlayer2sign(game.player2().sign());
        return entity;
    }

    public static void updateEntity(GameEntity entity, Game game) {
        entity.setFieldState(getFieldStateFromGame(game));
        entity.setState(game.state());
        List<TurnEntity> existingTurns = entity.getTurnsHistory();
        List<Turn> domainTurns = game.turns();

        if (existingTurns.size() < domainTurns.size()) {
            for (int i = existingTurns.size(); i < domainTurns.size(); i++) {
                Turn domainTurn = domainTurns.get(i);
                TurnEntity newTurn = TurnEntity.builder()
                        .row(domainTurn.getPosition().getRow())
                        .col(domainTurn.getPosition().getCol())
                        .playerId(domainTurn.getPlayer().playerId())
                        .sign(domainTurn.getPlayer().sign())
                        .game(entity)
                        .build();
                existingTurns.add(newTurn);
            }
        }
    }

    private static Field getFieldFromEntity(GameEntity entity) {
        String fieldState = entity.getFieldState();
        Field gameField = new Field();

        for (int i = 0; i < fieldState.length(); i++) {
            char cell = fieldState.charAt(i);
            int cellValue = cell - '0';
            FieldPosition pos = new FieldPosition(i / gameField.getRows(), i % gameField.getCols());
            gameField.setFieldValue(pos, cellValue);
        }
        return gameField;
    }

    private static List<Turn> getTurnsFromEntity(GameEntity entity) {
        List<Turn> turns = new ArrayList<>();
        List<TurnEntity> turnsHistory = entity.getTurnsHistory();
        for (TurnEntity turn: turnsHistory) {
            FieldPosition pos = new FieldPosition(turn.getRow(), turn.getCol());
            Player player = new Player(turn.getPlayerId(), turn.getSign());
            turns.add(new Turn(pos, player));
        }
        return turns;
    }

    private static String getFieldStateFromGame(Game game) {
        StringBuilder fieldState = new StringBuilder();
        Field field = game.field();
        int[][] matrix = field.getFieldMatrix();
        for (int i = 0; i < field.getRows(); i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < field.getCols(); j++) {
                row.append(matrix[i][j]);
            }
            fieldState.append(row.toString());
        }
        return fieldState.toString();
    }

    private static List<TurnEntity> getTurnsFromGame(Game game, GameEntity entity) {
        List<TurnEntity> turnEntities = new ArrayList<>();
        for (Turn turn : game.turns()) {
            TurnEntity turnEntity = TurnEntity.builder()
                    .row(turn.getPosition().getRow())
                    .col(turn.getPosition().getCol())
                    .playerId(turn.getPlayer().playerId())
                    .sign(turn.getPlayer().sign())
                    .game(entity)
                    .build();
            turnEntities.add(turnEntity);
        }

        return turnEntities;
    }


}
