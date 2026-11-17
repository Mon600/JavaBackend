package tictactoe.domain.service.game.player.Impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import tictactoe.datasource.service.GameRepositoryService;
import tictactoe.domain.model.*;
import tictactoe.domain.model.rules.GameState;
import tictactoe.domain.model.game.*;
import tictactoe.domain.model.rules.GameRules;
import tictactoe.domain.model.rules.GameSign;
import tictactoe.domain.service.game.player.GameService;
import tictactoe.security.FieldPosition;
import tictactoe.domain.service.auth.Impl.UserServiceImpl;
import tictactoe.domain.service.game.ai.AiService;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepositoryService repositoryService;
    private final AiService aiService;
    private final UserServiceImpl userServiceImpl;

    public GameServiceImpl(
            GameRepositoryService repositoryService, AiService aiService, UserServiceImpl userServiceImpl) {
        this.repositoryService = repositoryService;
        this.aiService = aiService;
        this.userServiceImpl = userServiceImpl;
    }

    @Transactional
    public Game createAiGame(UUID player1Id) {
        PlayerPair players = createPlayers(player1Id, Constants.aiId);
        Game game =
                firstAiTurn(new Game(players.player1(), players.player2(), GameState.PLAYER_TURN));
        return repositoryService.saveOrUpdateGame(game);
    }

    @Transactional
    public Game createPlayersGame(UUID player1Id, String player2Login) {
        UUID player2Id =
                userServiceImpl
                        .getUser(player2Login)
                        .orElseThrow(() -> new NoSuchElementException("User not found"))
                        .getUserId();
        if (player2Id.equals(player1Id)) {
            throw new IllegalStateException("You can't playing with yourself");
        }

        PlayerPair players = createPlayers(player1Id, player2Id);
        Game game = new Game(players.player1(), players.player2(), GameState.WAITING);
        return repositoryService.saveOrUpdateGame(game);
    }

    public Game firstAiTurn(Game game) {
        if (game.getTurnOrder().playerId().equals(Constants.aiId)) {
            Turn aiTurn = getNextTurn(game);
            return processAndCheckTurn(game, aiTurn);
        }
        return game;
    }

    @Transactional
    public Game acceptGame(UUID playerId, UUID gameId) {
        Game game = repositoryService.getGameById(gameId);
        if (!game.player2().playerId().equals(playerId)) {
            throw new IllegalArgumentException("You're not member of this game");
        }
        return repositoryService.saveOrUpdateGame(setStateGame(game, GameState.PLAYER_TURN));
    }

    @Transactional
    public Game getGame(UUID id) {
        return repositoryService.getGameById(id);
    }

    @Transactional
    public List<Game> getMyGames(UUID playerId) {
        return repositoryService.getGamesByUserId(playerId);
    }

    @Transactional
    public List<Game> getFinishedGames(UUID playerID) {
        return repositoryService.getAllFinishedGames(playerID);
    }

    @Transactional
    public List<Game> getNotAcceptedGames(UUID playerId) {
        return repositoryService.getNotAcceptedGames(playerId);
    }

    @Transactional
    public Game makeTurn(UUID currentPlayerId, UUID id, Turn turn) {
        Game game = repositoryService.getGameById(id);
        if (game.state() == GameState.WAITING) {
            throw new IllegalStateException("This game not start yet");
        }
        if (!currentPlayerId.equals(turn.player().playerId())) {
            throw new IllegalStateException("Your id not match with turn's player id");
        }
        GameState currentState = GameRules.checkGameEnd(game);
        if (currentState == GameState.PLAYER_WIN || currentState == GameState.AI_WIN || currentState == GameState.DRAW) {
            throw new IllegalStateException(
                    "This game is finished: " + game.state().getStateString());
        }

        if (!GameRules.isCorrectTurn(game, turn)) {
            throw new IllegalStateException("It's not your turn to move right now.");
        }

        if (!GameRules.isValidMove(game.field(), turn)) {
            throw new IllegalArgumentException("Incorrect turn. Try again.");
        }

        Game gameWithPlayerTurn =
                repositoryService.saveOrUpdateGame(processAndCheckTurn(game, turn));

        if (gameWithPlayerTurn.state() != GameState.PLAYER_TURN) {
            return gameWithPlayerTurn;
        }
        if (game.player1().playerId().equals(Constants.aiId)
                || game.player2().playerId().equals(Constants.aiId)) {
            Turn aiTurn = getNextTurn(gameWithPlayerTurn);
            return repositoryService.saveOrUpdateGame(
                    processAndCheckTurn(gameWithPlayerTurn, aiTurn));
        }
        return gameWithPlayerTurn;
    }

    @Transactional
    public GameSign getCurrentPlayerSign(UUID gameId, UUID playerId) {
        Game currentGame = repositoryService.getGameById(gameId);
        Player player1 = currentGame.player1();
        Player player2 = currentGame.player2();
        if (playerId.equals(player1.playerId())) {
            return player1.sign();
        } else if (playerId.equals(player2.playerId())) {
            return player2.sign();
        }
        throw new NoSuchElementException("You're not member of this game");
    }

    private Game processAndCheckTurn(Game currentGame, Turn turn) {
        FieldPosition pos = turn.position();
        Player player = turn.player();
        Game nextGame = currentGame.simulateTurn(new Turn(pos, player));
        GameState state = checkGameEnd(nextGame);

        return (state != GameState.PLAYER_TURN) ? setStateGame(nextGame, state) : nextGame;
    }

    @Override
    public Turn getNextTurn(Game game) {
        return aiService.calculateBestTurn(game);
    }

    @Override
    public GameState checkGameEnd(Game game) {
        return GameRules.checkGameEnd(game);
    }

    @Override
    public boolean validateField(Game game) {
        List<Turn> turns = game.turns();
        Field field = game.field();
        Field restoredField = new Field();
        for (Turn turn : turns) {
            int player1Turn = 1;
            int player2Turn = 2;
            restoredField.setFieldValue(
                    turn.position(),
                    (turn.player() == game.player1()) ? player1Turn : player2Turn);
        }
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getCols(); j++) {
                FieldPosition pos = new FieldPosition(i, j);
                if (field.getFieldValue(pos) != restoredField.getFieldValue(pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Game setStateGame(Game game, GameState state) {
        UUID winnerId;
        if (state.equals(GameState.AI_WIN) || state.equals(GameState.PLAYER_WIN)) {
            winnerId = game.turns().getLast().player().playerId();
        } else {
            winnerId = null;
        }
        return new Game(
                game.gameId(),
                game.field(),
                game.turns(),
                state,
                game.createdAt(),
                game.player1(),
                game.player2(),
                winnerId);
    }

    private PlayerPair createPlayers(UUID player1Id, UUID player2Id) {
        GameSign player1Sign = GameSign.getRandomSign();
        GameSign player2Sign = (player1Sign == GameSign.O) ? GameSign.X : GameSign.O;
        Player player1 = new Player(player1Id, player1Sign);
        Player player2 = new Player(player2Id, player2Sign);
        return new PlayerPair(player1, player2);
    }
}
