package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.RoundEndDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoundEndDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("roundEnd", RoundEndDTO.type);
    }

}
