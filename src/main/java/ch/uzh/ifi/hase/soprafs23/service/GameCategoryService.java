package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.game.GameCategory;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GameCategoryService {

    private final Logger log = LoggerFactory.getLogger(GameCategoryService.class);
    private final CategoryRepository categoryRepository;
    private final GameCategoryRepository gameCategoryRepository;

    @Autowired
    public GameCategoryService(@Qualifier("gameCategoryRepository")GameCategoryRepository gameCategoryRepository,
                               @Qualifier("categoryRepository") CategoryRepository categoryRepository) {
        this.gameCategoryRepository = gameCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public void saveCategories (Long gameId, List<String> categories) {
        GameCategory newGameCategory = new GameCategory();
        Long categoryId;
        for (String category : categories) {
            categoryId = categoryRepository.findByName(category).orElse(null);
            if (categoryId.equals(null)) {
                categoryRepository.save(category);
                categoryRepository.flush();
                categoryId = categoryRepository.findByName(category).orElse(null);
                log.debug("New category stored: {}", category);
            }
            newGameCategory.setCategoryId(categoryId);
            newGameCategory.setGameId(gameId);

            gameCategoryRepository.save(newGameCategory);
            gameCategoryRepository.flush();
        }

    }

}
