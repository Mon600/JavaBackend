package tictactoe.datasource.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tictactoe.datasource.model.GameEntity;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, UUID> {

    @Query("SELECT g FROM GameEntity g WHERE g.player1Id = :user_id OR g.player2Id = :user_id")
    public List<GameEntity> findByPlayerId(@Param("user_id") UUID user_id);

    @Query("SELECT g FROM GameEntity g WHERE g.player2Id = :user_id AND state = WAITING")
    public List<GameEntity> findAllByPlayer2IdWithState(@Param("user_id") UUID user_id);
}
