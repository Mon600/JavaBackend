package tictactoe.domain.service;


import tictactoe.domain.model.Game;
import tictactoe.domain.rules.GameState;
import tictactoe.domain.model.Turn;

public interface GameService {

    Turn getNextTurn(Game game);

    boolean validateField(Game game);

    GameState checkGameEnd(Game game);
}