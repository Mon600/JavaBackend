package tictactoe.web.mapper;

import tictactoe.domain.model.game.Player;
import tictactoe.web.model.game.PlayerRequest;



public class PlayerRequestDTO {
    public static PlayerRequest toPlayerRequest(Player player) {
        return PlayerRequest.builder()
                .playerId(player.getPlayerId().toString())
                .playerSign(player.getSign())
                .build();
    }


}
