package tictactoe.datasource.service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import tictactoe.datasource.mapper.GameMapper;
import tictactoe.datasource.model.GameEntity;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.domain.model.game.Game;

public class GameRepositoryService {
    private final GameRepository gameRepository;

    public GameRepositoryService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Game saveOrUpdateGame(Game game) {
        if (game.getGameId() == null) {
            GameEntity entity = GameMapper.toEntity(game);
            gameRepository.save(entity);
            return GameMapper.toGame(entity);
        } else {
            GameEntity entity =
                    gameRepository
                            .findById(game.getGameId())
                            .orElseThrow(() -> new NoSuchElementException("Game not found"));
            GameMapper.updateEntity(entity, game);
            gameRepository.save(entity);
            return GameMapper.toGame(entity);
        }
    }

    @Transactional(readOnly = true)
    public Game getGameById(UUID gameId) {
        Optional<GameEntity> game = gameRepository.findById(gameId);
        if (game.isPresent()) {
            GameEntity entity = game.get();
            return GameMapper.toGame(entity);
        }
        throw new NoSuchElementException(
                String.format("Game with ID %s not found", gameId.toString()));
    }

    @Transactional(readOnly = true)
    public List<Game> getAllFinishedGames(UUID userId) {
        return gameRepository.findAllFinishedGames(userId).stream()
                .map(GameMapper::toGame)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Game> getGamesByUserId(UUID userId) {
        return gameRepository.findByPlayerId(userId).stream().map(GameMapper::toGame).toList();
    }

    @Transactional(readOnly = true)
    public List<Game> getNotAcceptedGames(UUID userId) {
        return gameRepository.findAllByPlayer2IdWithState(userId).stream()
                .map(GameMapper::toGame)
                .toList();
    }
}
