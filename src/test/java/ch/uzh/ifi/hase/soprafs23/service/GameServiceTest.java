package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    void getStandardCategories_validInput() {

        AtomicReference<GameCategoriesDTO> gameCategoriesDTO = new AtomicReference<>();

        assertDoesNotThrow(() -> gameCategoriesDTO.set(gameService.getStandardCategories()));

        assertEquals(GameCategory.getCategories(), gameCategoriesDTO.get().getCategories());

    }

    @Test
    void getRandomCategory_validInput() {

        AtomicReference<String> categoryName = new AtomicReference<>();

        assertDoesNotThrow(() -> categoryName.set(gameService.getRandomCategory().getCategoryName()));

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
