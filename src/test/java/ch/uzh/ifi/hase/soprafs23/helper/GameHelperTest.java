package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs23.helper.GameHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class GameHelperTest {

    @Test
    void test_checkIfGameExists_gameIsNull() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfGameExists(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game does not exist. Please try again with a different game!", exception.getReason());

    }

    @Test
    void test_CheckIfGameExists_gameExists_noException() {

        Game game = new Game();

        assertDoesNotThrow(() -> checkIfGameExists(game));
    }

    @Test
    void test_CheckIfGameIsOpen_gameIsNotOpen() {

        Game game = new Game();
        game.setStatus(GameStatus.RUNNING);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfGameIsOpen(game));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Game is not open. Please try again with a different pin!",
                exception.getReason());

    }

    @Test
    void test_CheckIfGameIsOpen_gameIsOpen_noException() {

        Game game = new Game();
        game.setStatus(GameStatus.OPEN);

        assertDoesNotThrow(() -> checkIfGameIsOpen(game));
    }

    @Test
    void test_checkIfGameIsRunning_gameIsNotRunning() {

        Game game = new Game();
        game.setStatus(GameStatus.CLOSED);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfGameIsRunning(game));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Game is not running. Please try again with a different game!",
                exception.getReason());

    }

    @Test
    void test_checkIfGameIsRunning_gameIsRunning_noException() {

        Game game = new Game();
        game.setStatus(GameStatus.RUNNING);

        assertDoesNotThrow(() -> checkIfGameIsRunning(game));
    }

    @Test
    void test_checkIfUserIsInGame_userIsNotInGame() {

        Game game = new Game();
        User user = new User();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> checkIfUserIsInGame(game, user));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("User is not part of this game.",
                exception.getReason());

    }

    @Test
    void test_checkIfUserIsInGame_userIsInGame_noException() {

        Game game = new Game();
        User user = new User();
        game.addPlayer(user);

        assertDoesNotThrow(() -> checkIfUserIsInGame(game, user));
    }
    @Test
    public void checkIfNotToManyCategoriesTest() {
        // Create a list of 11 mocked categories
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            categories.add(Mockito.mock(Category.class));
        }

        // Mock a Game object
        Game game = Mockito.mock(Game.class);
        Mockito.when(game.getCategories()).thenReturn(categories);

        // Check that the method throws an exception with the correct status and message
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            checkIfNotToManyCategories(game);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void checkCategoryNamesTest() {
        // Create a list of categories with one having a name longer than MAX_STRING_LENGTH_OF_CATEGORY
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Category category = Mockito.mock(Category.class);
            Mockito.when(category.getName()).thenReturn("Short name");
            categories.add(category);
        }
        Category longNameCategory = Mockito.mock(Category.class);
        Mockito.when(longNameCategory.getName()).thenReturn("This name is definitely longer than the maximum allowed length");
        categories.add(longNameCategory);

        // Mock a Game object
        Game game = Mockito.mock(Game.class);
        Mockito.when(game.getCategories()).thenReturn(categories);

        // Check that the method throws an exception with the correct status and message
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            checkCategoryNames(game);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

}
