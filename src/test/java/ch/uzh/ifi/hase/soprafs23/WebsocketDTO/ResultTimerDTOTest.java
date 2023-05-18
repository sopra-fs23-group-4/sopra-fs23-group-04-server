package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.ResultTimerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResultTimerDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("resultTimer", ResultTimerDTO.TYPE);
    }

    @Test
    void getTimeRemaining_shouldReturnCorrectTimeRemaining() {
        ResultTimerDTO resultTimerDTO = new ResultTimerDTO();
        resultTimerDTO.setTimeRemaining(10);

        Assertions.assertEquals(10, resultTimerDTO.getTimeRemaining());
    }
}
