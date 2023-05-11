package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("categoryRepository")
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String categoryName);

}
