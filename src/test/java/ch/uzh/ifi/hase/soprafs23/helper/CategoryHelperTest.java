package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs23.helper.CategoryHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryHelperTest {
    @Test
    public void test_checkIfCategoryExists_categoryIsNull() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfCategoryExists(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("This category does not exist.", exception.getReason());

    }

    @Test
    public void test_checkIfCategoryExists_categoryExists_noException() {

        Category category = new Category();

        assertDoesNotThrow(() -> checkIfCategoryExists(category));
    }
}
