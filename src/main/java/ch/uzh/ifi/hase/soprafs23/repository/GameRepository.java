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

    @Query("SELECT gp.game FROM GameParticipant gp WHERE gp.user.id = :userId")
    List<Game> findAllGamesByUserId(@Param("userId") int userId);

    @Query("SELECT gp.user FROM GameParticipant gp WHERE gp.game.gamePin = :gamePin")
    List<User> findAllParticipantsByGamePin(@Param("gamePin") int gamePin);


}
