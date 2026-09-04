package tictactoe.domain.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenType {
    REFRESH("refresh"),
    ACCESS("access");

    private final String type;
}
