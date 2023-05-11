package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs23.constant.RoundStatus.FINISHED;

public class RoundHelper {

    private RoundHelper() {}

    public static void checkIfRoundExists(Round round) {

        String errorMessage = "Round does not exist.";

        if (round == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    public static void checkIfRoundIsFinished(Round round) {
        String errorMessage = "Round is not finished yet.";

        if (!round.getStatus().equals(FINISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }
}
