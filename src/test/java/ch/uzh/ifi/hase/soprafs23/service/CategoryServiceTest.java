package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {
    @Test
    void test_checkIfCategoryExists_categoryIsNull() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> CategoryService.checkIfCategoryExists(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("This category does not exist.", exception.getReason());

    }

    @Test
    void test_checkIfCategoryExists_categoryExists_noException() {

        Category category = new Category();

        assertDoesNotThrow(() -> CategoryService.checkIfCategoryExists(category));
    }

    @Test
    void getStandardCategories_validInput() {

        AtomicReference<GameCategoriesDTO> gameCategoriesDTO = new AtomicReference<>();

        assertDoesNotThrow(() -> gameCategoriesDTO.set(CategoryService.getStandardCategories()));

        assertEquals(GameCategory.getCategories(), gameCategoriesDTO.get().getCategories());

    }

    @Test
    void getRandomCategory_validInput() {

        AtomicReference<String> categoryName = new AtomicReference<>();

        assertDoesNotThrow(() -> categoryName.set(CategoryService.getRandomCategory().getCategoryName()));

        assertTrue(doesEnumExist(categoryName.get()));

    }

    public static boolean doesEnumExist(String value) {
        for (AdditionalCategory category : AdditionalCategory.values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
