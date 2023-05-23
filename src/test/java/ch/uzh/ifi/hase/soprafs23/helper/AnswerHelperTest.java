package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs23.helper.AnswerHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class AnswerHelperTest {

    @Test
    void test_checkIfAnswerExists_answerIsNull() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfAnswerExists(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("This answer does not exist.", exception.getReason());

    }

    @Test
    void test_checkIfAnswerExists_answerExists_noException() {

        Answer answer = new Answer();

        assertDoesNotThrow(() -> checkIfAnswerExists(answer));
    }
    @Test
    void test_checkIfAnswerIsTooLong_true() {
        String answer ="jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj";
        assertTrue(AnswerHelper.isAnswerTooLong(answer));
    }

    @Test
    void test_checkIfAnswerIsTooLong_false() {
        String answer="Hello! How are you today?";
        assertFalse(AnswerHelper.isAnswerTooLong(answer));
    }

    @Test
    void test_shortensTooLongAnswer() {
        String answer ="jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj";
        String shortendAnswer=AnswerHelper.shortenTooLongAnswer(answer);
        assertEquals(Constant.ANSWER_MAX_LENGTH,shortendAnswer.length());
    }

}
