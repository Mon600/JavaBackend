package tictactoe.domain.service.game.player;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import tictactoe.datasource.service.GameRepositoryService;
import tictactoe.domain.model.*;
import tictactoe.domain.model.game.GameState;
import tictactoe.domain.model.game.*;
import tictactoe.domain.model.security.FieldPosition;
import tictactoe.domain.service.auth.UserService;
import tictactoe.domain.service.game.ai.AiService;

public class GameService implements GameInterface {
    GameRepositoryService repositoryService;
    AiService aiService;
    UserService userService;

    public GameService(
            GameRepositoryService repositoryService, AiService aiService, UserService userService) {
        this.repositoryService = repositoryService;
        this.aiService = aiService;
        this.userService = userService;
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
                userService
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
        if (game.getTurnOrder().getPlayerId().equals(Constants.aiId)) {
            Turn aiTurn = getNextTurn(game);
            return processAndCheckTurn(game, aiTurn);
        }
        return game;
    }

    @Transactional
    public Game acceptGame(UUID playerId, UUID gameId) {
        Game game = repositoryService.getGameById(gameId);
        if (!game.getPlayer2().getPlayerId().equals(playerId)) {
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
        if (game.getState() == GameState.WAITING) {
            throw new IllegalStateException("This game not start yet");
        }
        if (!currentPlayerId.equals(turn.getPlayer().getPlayerId())) {
            throw new IllegalStateException("Your id not match with turn's player id");
        }
        GameState currentState = GameRules.checkGameEnd(game);
        if (currentState == GameState.PLAYER_WIN || currentState == GameState.AI_WIN || currentState == GameState.DRAW) {
            throw new IllegalStateException(
                    "This game is finished: " + game.getState().getStateString());
        }

        if (!GameRules.isCorrectTurn(game, turn)) {
            throw new IllegalStateException("It's not your turn to move right now.");
        }

        if (!GameRules.isValidMove(game.getField(), turn)) {
            throw new IllegalArgumentException("Incorrect turn. Try again.");
        }

        Game gameWithPlayerTurn =
                repositoryService.saveOrUpdateGame(processAndCheckTurn(game, turn));

        if (gameWithPlayerTurn.getState() != GameState.PLAYER_TURN) {
            return gameWithPlayerTurn;
        }
        if (game.getPlayer1().getPlayerId().equals(Constants.aiId)
                || game.getPlayer2().getPlayerId().equals(Constants.aiId)) {
            Turn aiTurn = getNextTurn(gameWithPlayerTurn);
            return repositoryService.saveOrUpdateGame(
                    processAndCheckTurn(gameWithPlayerTurn, aiTurn));
        }
        return gameWithPlayerTurn;
    }

    @Transactional
    public GameSign getCurrentPlayerSign(UUID gameId, UUID playerId) {
        Game currentGame = repositoryService.getGameById(gameId);
        Player player1 = currentGame.getPlayer1();
        Player player2 = currentGame.getPlayer2();
        if (playerId.equals(player1.getPlayerId())) {
            return player1.getSign();
        } else if (playerId.equals(player2.getPlayerId())) {
            return player2.getSign();
        }
        throw new NoSuchElementException("You're not member of this game");
    }

    private Game processAndCheckTurn(Game currentGame, Turn turn) {
        FieldPosition pos = turn.getPosition();
        Player player = turn.getPlayer();
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
        List<Turn> turns = game.getTurns();
        Field field = game.getField();
        Field restoredField = new Field();
        for (Turn turn : turns) {
            int player1Turn = 1;
            int player2Turn = 2;
            restoredField.setFieldValue(
                    turn.getPosition(),
                    (turn.getPlayer() == game.getPlayer1()) ? player1Turn : player2Turn);
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
            winnerId = game.getTurns().getLast().getPlayer().getPlayerId();
        } else {
            winnerId = null;
        }
        return new Game(
                game.getGameId(),
                game.getField(),
                game.getTurns(),
                state,
                game.getCreatedAt(),
                game.getPlayer1(),
                game.getPlayer2(),
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
