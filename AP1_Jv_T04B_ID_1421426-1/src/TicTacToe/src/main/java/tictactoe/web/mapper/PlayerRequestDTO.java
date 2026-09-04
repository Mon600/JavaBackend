package tictactoe.web.mapper;

import tictactoe.domain.model.GameSign;
import tictactoe.domain.model.Player;
import tictactoe.web.model.PlayerRequest;

import java.util.UUID;

public class PlayerRequestDTO {
    public static PlayerRequest toPlayerRequest(Player player) {
        return PlayerRequest.builder()
                .playerSign(player.getSign())
                .build();
    }

    public static Player toPlayer(PlayerRequest DTO) {
        GameSign sign = DTO.getPlayerSign();
        UUID id = UUID.fromString(DTO.getPlayerId());
        return new Player(id, sign);
    }
}
