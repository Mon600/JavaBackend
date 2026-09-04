package tictactoe.datasource.model;

import jakarta.persistence.*;
import lombok.*;
import tictactoe.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID gameId;

    @Column(name = "field_state", nullable = false, length = 9)
    @Builder.Default
    private String fieldState = "000000000";

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TurnEntity> turnsHistory = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private GameState state;

    @Column(name = "player_1_id", nullable = false)
    private UUID player1Id;

    @Column(name = "player_2_id", nullable = false)
    private UUID player2Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_1_sign", nullable = false)
    private GameSign player1sign;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_2_sign", nullable = false)
    private GameSign player2sign;

    @Version
    @Column(name = "version")
    private Long version;
}