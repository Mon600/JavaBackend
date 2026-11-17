package tictactoe.mapper;

import tictactoe.domain.rules.GameSign;
import tictactoe.domain.model.Player;
import tictactoe.web.dto.PlayerRequest;

import java.util.UUID;

public class PlayerRequestDTO {
    public static PlayerRequest toPlayerRequest(Player player) {
        return PlayerRequest.builder()
                .playerSign(player.sign())
                .build();
    }

    public static Player toPlayer(PlayerRequest DTO) {
        GameSign sign = DTO.getPlayerSign();
        UUID id = UUID.fromString(DTO.getPlayerId());
        return new Player(id, sign);
    }
}
