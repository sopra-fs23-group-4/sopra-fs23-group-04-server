package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class VoteHelper {

    private VoteHelper() {}
    public static void checkIfVotingAlreadyExists(Vote vote) {

        String errorMessage = "This voting already exists.";

        if (vote != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
}
