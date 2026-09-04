package tictactoe.datasource.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import tictactoe.domain.model.GameSign;




import java.util.UUID;

@Entity
@Table(name = "turns")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TurnEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long turnId;

    @Column(name="turn_row", nullable = false)
    private int row;

    @Column(name="turn_col", nullable = false)
    private int col;


    @Column(name = "player", nullable = false)
    private UUID playerId;

    @Enumerated(EnumType.STRING)
    @Column(name =  "sign", nullable = false)
    private GameSign sign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

}
