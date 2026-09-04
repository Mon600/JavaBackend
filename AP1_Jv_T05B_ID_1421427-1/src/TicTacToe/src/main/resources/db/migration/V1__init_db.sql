

CREATE TABLE games (
    game_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    state VARCHAR(15) NOT NULL,
    player_1_id uuid NOT NULL,
    player_2_id uuid NOT NULL,
    player_1_sign VARCHAR(1) NOT NULL,
    player_2_sign VARCHAR(1) NOT NULL,
    winner_id uuid DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT check_different_signs CHECK (player_1_sign != player_2_sign)
);


CREATE TABLE turns (
    turn_id BIGSERIAL PRIMARY KEY,
    turn_row INTEGER NOt NULL,
    turn_col INTEGER NOT NULL,
    player uuid NOT NULL,
    sign VARCHAR(1) NOT NULL,
    game_id uuid NOT NULL,
    CONSTRAINT fk_game
        FOREIGN KEY(game_id)
        REFERENCES games(game_id)
        ON DELETE CASCADE
);

CREATE TABLE users (
    user_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    login VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL
);

CREATE TABLE roles (
    user_id uuid NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role VARCHAR(28) NOT NULL,

    PRIMARY KEY(user_id, role)
);