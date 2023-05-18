package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.ScoreboardTimerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScoreBoardTimerDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("scoreboardTimer", ScoreboardTimerDTO.TYPE);
    }

    @Test
    void getTimeRemaining_shouldReturnCorrectTimeRemaining() {
        ScoreboardTimerDTO scoreboardTimerDTO = new ScoreboardTimerDTO();
        scoreboardTimerDTO.setTimeRemaining(10);

        Assertions.assertEquals(10, scoreboardTimerDTO.getTimeRemaining());
    }
}
