package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("gameCategoryRepository")
public interface GameCategoryRepository extends JpaRepository<GameCategory, Long> {

    List<Long> getCategoriesByGameId(Long gameId);
}
