package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.WebSocketDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WebSocketDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        WebSocketDTO webSocketDTO = new WebSocketDTO();
        webSocketDTO.setType("testType");

        Assertions.assertEquals("testType", webSocketDTO.getType());
    }
}
