package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class GameHelperTest {

    private GameHelper gameHelper;

    @BeforeEach
    public void setUp() {
        gameHelper = new GameHelper();
    }

    @Test
    public void test_checkIfGameExists_gameIsNull() {

        Game game = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameHelper.checkIfGameExists(game));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game does not exist. Please try again with a different game!", exception.getReason());

    }

    @Test
    public void testCheckIfGameExists_gameExists_noException() {

        Game game = new Game();

        assertDoesNotThrow(() -> gameHelper.checkIfGameExists(game));
    }

    @Test
    public void testCheckIfGameIsOpen_gameIsNotOpen() {

        Game game = new Game();
        game.setStatus(GameStatus.RUNNING);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameHelper.checkIfGameIsOpen(game));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Game is not open. Please try again with a different pin!",
                exception.getReason());

    }

    @Test
    public void testCheckIfGameIsOpen_gameIsOpen_noException() {

        Game game = new Game();
        game.setStatus(GameStatus.OPEN);

        assertDoesNotThrow(() -> gameHelper.checkIfGameIsOpen(game));
    }

    @Test
    public void checkIfGameIsRunning_gameIsNotRunning() {

        Game game = new Game();
        game.setStatus(GameStatus.CLOSED);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameHelper.checkIfGameIsRunning(game));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Game is not running. Please try again with a different game!",
                exception.getReason());

    }

    @Test
    public void checkIfGameIsRunning_gameIsRunning_noException() {

        Game game = new Game();
        game.setStatus(GameStatus.RUNNING);

        assertDoesNotThrow(() -> gameHelper.checkIfGameIsRunning(game));
    }

    @Test
    public void checkIfUserIsInGame_userIsNotInGame() {

        Game game = new Game();
        User user = new User();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameHelper.checkIfUserIsInGame(game, user));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("User is not part of this game.",
                exception.getReason());

    }

    @Test
    public void checkIfUserIsInGame_userIsInGame_noException() {

        Game game = new Game();
        User user = new User();
        game.addPlayer(user);

        assertDoesNotThrow(() -> gameHelper.checkIfUserIsInGame(game, user));
    }
}
