package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.AdvancedStatisticGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.atomic.AtomicReference;

import static ch.uzh.ifi.hase.soprafs23.helper.VoteHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class VoteHelperTest {

    @Test
    void test_checkIfVotingExists_voteIsNull() {

        Vote vote = new Vote();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfVotingAlreadyExists(vote));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("This voting already exists.", exception.getReason());

    }

    @Test
    void test_checkIfVotingExists_voteExists_noException() {

        assertDoesNotThrow(() -> checkIfVotingAlreadyExists(null));
    }

    @Test
    void test_getVoteOptions_noException() {

        AtomicReference<VoteOptionsGetDTO> voteOptionsGetDTO = new AtomicReference<>();
        assertDoesNotThrow(() -> voteOptionsGetDTO.set(getVoteOptions()));

        assertEquals(4, voteOptionsGetDTO.get().getVoteOptions().size());
    }

}
