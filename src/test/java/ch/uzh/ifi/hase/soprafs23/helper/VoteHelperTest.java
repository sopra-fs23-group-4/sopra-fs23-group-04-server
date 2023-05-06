package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs23.helper.VoteHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class VoteHelperTest {

    @Test
    public void test_checkIfVotingExists_voteIsNull() {

        Vote vote = new Vote();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfVotingAlreadyExists(vote));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("This voting already exists.", exception.getReason());

    }

    @Test
    public void test_checkIfVotingExists_voteExists_noException() {

        assertDoesNotThrow(() -> checkIfVotingAlreadyExists(null));
    }
}
