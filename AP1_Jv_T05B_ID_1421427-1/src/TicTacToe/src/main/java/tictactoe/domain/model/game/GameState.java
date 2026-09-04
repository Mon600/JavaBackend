package tictactoe.domain.model.game;

import lombok.Getter;

@Getter
public enum GameState {
    WAITING("waiting..."),
    PLAYER_TURN("Turning player"),
    DRAW("Draw"),
    PLAYER_WIN("Won player"),
    AI_WIN("AI won");

    private final String stateString;

    GameState(String stateString) {
        this.stateString = stateString;
    }
}
