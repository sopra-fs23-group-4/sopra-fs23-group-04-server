package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class UserHelperTest {

    private UserHelper userHelper;

    @BeforeEach
    public void setUp() {
        userHelper = new UserHelper();
    }

    @Test
    public void test_checkIfUserExists_userIsNull() {

        User user = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userHelper.checkIfUserExists(user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User does not exist. Please register before playing!", exception.getReason());

    }

    @Test
    public void checkIfUserExists_userExists_noException() {

        User user = new User();

        assertDoesNotThrow(() -> userHelper.checkIfUserExists(user));
    }
}
