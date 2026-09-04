package tictactoe.datasource.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tictactoe.datasource.model.UserEntity;

public interface StatRepository extends CrudRepository<UserEntity, UUID> {

    @Query(
            value = """
            SELECT
                u.user_id,
                u.login,
                COUNT(CASE WHEN total_cnt.winner_id = u.user_id THEN 1 END) AS wins_counter,
                COALESCE(
                    1.0 * COUNT(CASE WHEN total_cnt.winner_id = u.user_id THEN 1 END)
                    / NULLIF(
                        COUNT(CASE WHEN total_cnt.winner_id != u.user_id
                                     OR total_cnt.winner_id IS NULL THEN 1 END),
                        0
                      ),
                    1
                ) AS WLD
            FROM users AS u
            JOIN games AS total_cnt
              ON (total_cnt.player_1_id = u.user_id OR total_cnt.player_2_id = u.user_id)
             AND total_cnt.state != 'WAITING'
             AND total_cnt.state != 'PLAYER_TURN'
            GROUP BY u.user_id, u.login
            ORDER BY WLD DESC
            LIMIT :limit
            """,
            nativeQuery = true
    )
    List<Object[]> getLeaders(@Param("limit") int limit);
}