package tictactoe.domain.service.game.player;

import tictactoe.domain.model.game.Game;
import tictactoe.domain.model.game.GameState;
import tictactoe.domain.model.game.Turn;

public interface GameInterface {

    Turn getNextTurn(Game game);

    boolean validateField(Game game);

    GameState checkGameEnd(Game game);
}
