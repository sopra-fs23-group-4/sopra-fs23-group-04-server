package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.RoundTimerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoundTimerDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("roundTimer", RoundTimerDTO.type);
    }

    @Test
    void getTimeRemaining_shouldReturnCorrectTimeRemaining() {
        RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
        roundTimerDTO.setTimeRemaining(10);

        Assertions.assertEquals(10, roundTimerDTO.getTimeRemaining());
    }
}
