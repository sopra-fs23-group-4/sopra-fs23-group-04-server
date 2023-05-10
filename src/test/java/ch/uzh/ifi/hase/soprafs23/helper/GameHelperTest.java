package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
}
