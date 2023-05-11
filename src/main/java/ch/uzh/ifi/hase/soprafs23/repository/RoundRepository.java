package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("roundRepository")
public interface RoundRepository extends JpaRepository<Round, Integer> {

    List<Round> findByGame(Game game);

    Round findByGameAndRoundNumber(Game game, int roundNumber);


}
