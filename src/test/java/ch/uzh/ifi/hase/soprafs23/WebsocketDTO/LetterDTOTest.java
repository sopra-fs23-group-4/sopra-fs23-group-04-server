package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.LetterDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LetterDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("roundStart", LetterDTO.type);
    }

    @Test
    void getRound_shouldReturnCorrectRound() {
        LetterDTO letterDTO = new LetterDTO();
        letterDTO.setRound(1);

        Assertions.assertEquals(1, letterDTO.getRound());
    }

    @Test
    void getLetter_shouldReturnCorrectLetter() {
        LetterDTO letterDTO = new LetterDTO();
        letterDTO.setLetter('A');

        Assertions.assertEquals('A', letterDTO.getLetter());
    }
}
