package tictactoe.domain.service.game.player;

import tictactoe.domain.model.game.Game;
import tictactoe.domain.model.rules.GameState;
import tictactoe.domain.model.game.Turn;

public interface GameService {

    Turn getNextTurn(Game game);

    boolean validateField(Game game);

    GameState checkGameEnd(Game game);
}
