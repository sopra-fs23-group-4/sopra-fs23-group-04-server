package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.VotingTimerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VotingTimerDTOTest {
    @Test
    public void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("votingTimer", VotingTimerDTO.TYPE);
    }

    @Test
    public void getTimeRemaining_shouldReturnCorrectTimeRemaining() {
        VotingTimerDTO votingTimerDTO = new VotingTimerDTO();
        votingTimerDTO.setTimeRemaining(10);

        Assertions.assertEquals(10, votingTimerDTO.getTimeRemaining());
    }
}
