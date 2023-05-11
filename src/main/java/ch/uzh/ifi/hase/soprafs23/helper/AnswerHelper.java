package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AnswerHelper {

    private AnswerHelper() {}

    public static void checkIfAnswerExists(Answer answer) {

        String errorMessage = "This answer does not exist.";

        if (answer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }
}
