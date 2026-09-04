package tictactoe.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tictactoe.domain.model.game.GameSign;
import tictactoe.domain.model.security.JwtAuthentication;
import tictactoe.domain.service.game.player.GameService;
import tictactoe.web.mapper.WebDTOMapper;
import tictactoe.web.model.game.CreateGameRequest;
import tictactoe.web.model.game.TurnRequest;
import tictactoe.web.model.game.WebDTO;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public WebDTO createGame(
            Authentication authentication, @RequestBody CreateGameRequest request) {
        UUID player1Id = UUID.fromString(authentication.getName());
        if (request.isVsAi()) {
            return WebDTOMapper.toResponse(gameService.createAiGame(player1Id));
        } else {
            return WebDTOMapper.toResponse(
                    gameService.createPlayersGame(player1Id, request.getPlayer2Login()));
        }
    }

    @GetMapping("/my-games")
    public List<WebDTO> getMyGames(Authentication authentication) {
        UUID playerId = UUID.fromString(authentication.getName());
        return gameService.getMyGames(playerId).stream().map(WebDTOMapper::toResponse).toList();
    }

    @GetMapping("/{gameId}")
    public WebDTO getGame(@PathVariable("gameId") UUID gameId) {
        return WebDTOMapper.toResponse(gameService.getGame(gameId));
    }

    @PostMapping("/{gameId}/accept")
    public WebDTO acceptGame(Authentication authentication, @PathVariable("gameId") UUID gameId) {
        UUID playerId = UUID.fromString(authentication.getName());
        return WebDTOMapper.toResponse(gameService.acceptGame(playerId, gameId));
    }

    @PostMapping("/{gameId}/move")
    public WebDTO makeMove(
            Authentication authentication,
            @PathVariable("gameId") UUID gameId,
            @Valid @RequestBody TurnRequest request) {
        UUID currentPlayerId = UUID.fromString(authentication.getName());
        GameSign currentPlayerSign = gameService.getCurrentPlayerSign(gameId, currentPlayerId);
        return WebDTOMapper.toResponse(
                gameService.makeTurn(
                        currentPlayerId,
                        gameId,
                        WebDTOMapper.getTurnGame(request, currentPlayerId, currentPlayerSign)));
    }

    @GetMapping("/finished")
    public List<WebDTO> getFinishedGames(JwtAuthentication authentication) {
        UUID uuid = authentication.getPrincipal();
        return gameService.getFinishedGames(uuid).stream().map(WebDTOMapper::toResponse).toList();
    }

    @GetMapping("/games/not-accepted")
    public List<WebDTO> getNotAcceptedGames(Authentication authentication) {
        UUID playerId = UUID.fromString(authentication.getName());
        return gameService.getNotAcceptedGames(playerId).stream()
                .map(WebDTOMapper::toResponse)
                .toList();
    }
}
