package tictactoe.datasource.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import tictactoe.domain.model.game.GameSign;
import tictactoe.domain.model.game.GameState;

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

    @Column(name = "winner_id", nullable = true)
    private UUID winnerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version")
    private Long version;
}
