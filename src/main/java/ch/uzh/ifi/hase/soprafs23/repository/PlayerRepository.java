package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player,Long> {
    Player findByToken (String token);
}
