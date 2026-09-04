package tictactoe.datasource.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String hashedPassword;
}
