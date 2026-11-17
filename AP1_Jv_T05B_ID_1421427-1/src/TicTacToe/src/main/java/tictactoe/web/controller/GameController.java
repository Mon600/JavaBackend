package tictactoe.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tictactoe.domain.model.rules.GameSign;
import tictactoe.security.JwtAuthentication;
import tictactoe.domain.service.game.player.Impl.GameServiceImpl;
import tictactoe.mapper.WebDTOMapper;
import tictactoe.web.dto.game.CreateGameRequest;
import tictactoe.web.dto.game.TurnRequest;
import tictactoe.web.dto.game.WebDTO;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameServiceImpl gameServiceImpl;

    public GameController(GameServiceImpl gameServiceImpl) {
        this.gameServiceImpl = gameServiceImpl;
    }

    @PostMapping("/create")
    public WebDTO createGame(
            Authentication authentication, @RequestBody CreateGameRequest request) {
        UUID player1Id = UUID.fromString(authentication.getName());
        if (request.isVsAi()) {
            return WebDTOMapper.toResponse(gameServiceImpl.createAiGame(player1Id));
        } else {
            return WebDTOMapper.toResponse(
                    gameServiceImpl.createPlayersGame(player1Id, request.getPlayer2Login()));
        }
    }

    @GetMapping("/my-games")
    public List<WebDTO> getMyGames(Authentication authentication) {
        UUID playerId = UUID.fromString(authentication.getName());
        return gameServiceImpl.getMyGames(playerId).stream().map(WebDTOMapper::toResponse).toList();
    }

    @GetMapping("/{gameId}")
    public WebDTO getGame(@PathVariable("gameId") UUID gameId) {
        return WebDTOMapper.toResponse(gameServiceImpl.getGame(gameId));
    }

    @PostMapping("/{gameId}/accept")
    public WebDTO acceptGame(Authentication authentication, @PathVariable("gameId") UUID gameId) {
        UUID playerId = UUID.fromString(authentication.getName());
        return WebDTOMapper.toResponse(gameServiceImpl.acceptGame(playerId, gameId));
    }

    @PostMapping("/{gameId}/move")
    public WebDTO makeMove(
            Authentication authentication,
            @PathVariable("gameId") UUID gameId,
            @Valid @RequestBody TurnRequest request) {
        UUID currentPlayerId = UUID.fromString(authentication.getName());
        GameSign currentPlayerSign = gameServiceImpl.getCurrentPlayerSign(gameId, currentPlayerId);
        return WebDTOMapper.toResponse(
                gameServiceImpl.makeTurn(
                        currentPlayerId,
                        gameId,
                        WebDTOMapper.getTurnGame(request, currentPlayerId, currentPlayerSign)));
    }

    @GetMapping("/finished")
    public List<WebDTO> getFinishedGames(JwtAuthentication authentication) {
        UUID uuid = authentication.getPrincipal();
        return gameServiceImpl.getFinishedGames(uuid).stream().map(WebDTOMapper::toResponse).toList();
    }

    @GetMapping("/games/not-accepted")
    public List<WebDTO> getNotAcceptedGames(Authentication authentication) {
        UUID playerId = UUID.fromString(authentication.getName());
        return gameServiceImpl.getNotAcceptedGames(playerId).stream()
                .map(WebDTOMapper::toResponse)
                .toList();
    }
}
