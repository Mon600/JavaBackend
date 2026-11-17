package tictactoe.mapper;

import tictactoe.domain.model.game.Player;
import tictactoe.web.dto.game.PlayerRequest;



public class PlayerRequestDTO {
    public static PlayerRequest toPlayerRequest(Player player) {
        return PlayerRequest.builder()
                .playerId(player.playerId().toString())
                .playerSign(player.sign())
                .build();
    }


}
