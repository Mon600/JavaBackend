package tictactoe.domain.model.game;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;

@Getter
public enum GameSign {
    X("X"),
    O("O");

    private final String signString;

    GameSign(String signString) {
        this.signString = signString;
    }

    public static GameSign getRandomSign() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }
}
