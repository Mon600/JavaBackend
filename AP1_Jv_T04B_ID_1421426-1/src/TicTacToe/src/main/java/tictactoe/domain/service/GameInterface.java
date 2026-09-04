package tictactoe.domain.service;


import tictactoe.domain.model.Game;
import tictactoe.domain.model.GameState;
import tictactoe.domain.model.Turn;

public interface GameInterface {

    Turn getNextTurn(Game game);

    boolean validateField(Game game);

    GameState checkGameEnd(Game game);
}