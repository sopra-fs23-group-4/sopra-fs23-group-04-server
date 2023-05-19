package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.game.SkipManager;
import ch.uzh.ifi.hase.soprafs23.repository.SkipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class SkipManagerTest {
    private SkipManager skipManager;
    private User user;

    private User user2;

    @BeforeEach
    public void setup() {
        skipManager = new SkipManager();
        user = Mockito.mock(User.class);
        user2 = Mockito.mock(User.class);
    }

    @Test
    public void testCleanUp() {
        skipManager.addUser(user);
        skipManager.userWantsToSkip(user);
        skipManager.cleanUp();
        assertFalse(skipManager.allPlayersWantToContinue());
    }

    @Test
    public void testAllPlayersWantToContinue() {
        skipManager.addUser(user);
        skipManager.addUser(user);
        skipManager.userWantsToSkip(user2);
        assertFalse(skipManager.allPlayersWantToContinue());
        skipManager.userWantsToSkip(user);
        assertTrue(skipManager.allPlayersWantToContinue());
        skipManager.cleanUp();
        assertFalse(skipManager.allPlayersWantToContinue());
    }

    @Test
    public void testCleanFunction() {
        skipManager.addUser(user);
        skipManager.addUser(user);
        skipManager.userWantsToSkip(user2);
        skipManager.cleanUp();
        skipManager.userWantsToSkip(user);
        assertFalse(skipManager.allPlayersWantToContinue());
    }

    @Test
    public void testUserWantsToSkipAlreadyWantsToSkip() {
        skipManager.addUser(user);
        skipManager.userWantsToSkip(user);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> skipManager.userWantsToSkip(user));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    public void testAddUser() {
        skipManager.addUser(user);
        assertFalse(skipManager.allPlayersWantToContinue());
    }

    @Test
    public void testRemoveUser() {
        skipManager.addUser(user);
        skipManager.removeUser(user);
        assertTrue(skipManager.allPlayersWantToContinue());
    }
}
