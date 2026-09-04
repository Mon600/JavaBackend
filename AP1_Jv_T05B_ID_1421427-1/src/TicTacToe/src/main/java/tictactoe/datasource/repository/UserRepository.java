package tictactoe.datasource.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tictactoe.datasource.model.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByLogin(String login);
}
