package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Integer> {

    Game findByGameId(int gameId);
    Game findByGamePin(int gamePin);
    List<Game> findByStatus(GameStatus status);
    @Query("SELECT g FROM Game g JOIN g.users u WHERE u.id = :userId")
    List<Game> findAllGamesByUserId(@Param("userId") int userId);

    @Query("SELECT g.users FROM Game g WHERE g.gamePin = :gamePin")
    List<User> findAllUsersByGamePin(@Param("gamePin") int gamePin);


}
