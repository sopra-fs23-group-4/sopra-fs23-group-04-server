package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.CategoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {

    public static CategoryRepository categoryRepository;

    @Autowired
    private CategoryService(CategoryRepository categoryRepository) {
        CategoryService.categoryRepository = categoryRepository;
    }

    public static void checkIfCategoryExists(Category category) {

        String errorMessage = "This category does not exist.";

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }

    public static GameCategoriesDTO getStandardCategories() {

        GameCategoriesDTO gameCategoriesDTO = new GameCategoriesDTO();
        gameCategoriesDTO.setCategories(GameCategory.getCategories());

        return gameCategoriesDTO;
    }

    public static CategoryGetDTO getRandomCategory() {

        CategoryGetDTO categoryGetDTO = new CategoryGetDTO();
        String randomCategoryName = AdditionalCategory.getRandomCategoryName();
        while(randomCategoryName.length() > 18) {
            randomCategoryName = AdditionalCategory.getRandomCategoryName();
        }
        categoryGetDTO.setCategoryName(randomCategoryName);

        return categoryGetDTO;
    }

}
