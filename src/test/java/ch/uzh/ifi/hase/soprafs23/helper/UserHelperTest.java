package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs23.helper.GameHelper.checkIfGameIsOpen;
import static ch.uzh.ifi.hase.soprafs23.helper.UserHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class UserHelperTest {

    @Test
    void test_checkIfUserExists_userIsNull() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> checkIfUserExists(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User does not exist. Please register before playing!", exception.getReason());

    }

    @Test
    void test_checkIfUserExists_userExists_noException() {

        User user = new User();

        assertDoesNotThrow(() -> checkIfUserExists(user));
    }

    @Test
    void test_checkIfQuoteValid_quoteValid() {

        String tooLongQuote = "This quote is good!";

        assertDoesNotThrow(() -> checkIfQuoteValid(tooLongQuote));

    }

    @Test
    void test_checkIfQuoteValid_quoteTooLong() {

        String tooLongQuote =
                        "This quote is way, way, way, way, way, way, way, way, way, way, " +
                        "way, way, way, way, way, way, way, way, way, way, way, way, way, " +
                        "way, way, way, way, way, way, way, way, way, way, way, way, way, " +
                        "way, way, way, way, way, way, way, way, way, way, way, way, way, " +
                        "way, way, way, way, way, way, way, way, way, way, way, way, way, " +
                        "waaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaay too long!";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> checkIfQuoteValid(tooLongQuote));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

}
