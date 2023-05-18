package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.FactDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactDTOTest {

    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("fact", FactDTO.TYPE);
    }

    @Test
    void getFact_shouldReturnCorrectFact() {
        FactDTO factDTO = new FactDTO();
        factDTO.setFact("Some fact");

        Assertions.assertEquals("Some fact", factDTO.getFact());
    }
}