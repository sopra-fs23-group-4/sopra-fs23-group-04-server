package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs23.helper.AnswerHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerHelperTest {

    @Test
    public void test_checkIfAnswerExists_answerIsNull() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfAnswerExists(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("This answer does not exist.", exception.getReason());

    }

    @Test
    public void test_checkIfAnswerExists_answerExists_noException() {

        Answer answer = new Answer();

        assertDoesNotThrow(() -> checkIfAnswerExists(answer));
    }

}
