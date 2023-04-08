package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.GameUserMap;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("gameUserMapRepository")
public interface GameUserMapRepository extends JpaRepository<GameUserMap, Long> {

    @Query("SELECT g.game FROM GameUserMap g WHERE g.user.id = :userId")
    List<Game> findAllGamesByUserId(Long userId);

}
