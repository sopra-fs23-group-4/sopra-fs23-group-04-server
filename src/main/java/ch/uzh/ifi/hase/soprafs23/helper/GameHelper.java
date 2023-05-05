package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.RUNNING;

public class GameHelper {

    public static void checkIfGameExists(Game game) {

        String errorMessage = "Game does not exist. Please try again with a different game!";

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public static void checkIfGameIsOpen(Game game) {

        String errorMessage = "Game is not open. " +
                "Please try again with a different pin!";

        if (!game.getStatus().equals(GameStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    public static void checkIfGameIsRunning(Game game) {
        String errorMessage = "Game is not running. Please try again with a different game!";

        if (!game.getStatus().equals(RUNNING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    public static void checkIfUserIsInGame(Game game, User user) {
        List<User> users = game.getUsers();

        String errorMessage = "User is not part of this game.";

        if(!users.contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }
}