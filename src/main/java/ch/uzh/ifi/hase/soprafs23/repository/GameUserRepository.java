package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("gameUserRepository")
public interface GameUserRepository {

    List<Long> findByGameId(Long gameId);
}
