package ch.uzh.ifi.hase.soprafs23.serviceIntegration;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 * @see UserService
 */


@WebAppConfiguration
@SpringBootTest
class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean // Add this annotation to mock the QuoteService
    private QuoteService quoteService;

    @BeforeEach
    public void setup() { userRepository.deleteAll(); }

    @Test
    void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("user1"));

    User testUser = new User();

    testUser.setUsername("user1");
    testUser.setPassword("alpha");
    testUser.setCreationDate(LocalDate.EPOCH);

    // when
    User createdUser = userService.createAndReturnUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());

    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("user1"));

    User testUser = new User();
    testUser.setPassword("alpha");
    testUser.setCreationDate(LocalDate.EPOCH);

    testUser.setUsername("user1");
    User createdUser = userService.createAndReturnUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username

    testUser2.setUsername("user1");
    testUser2.setPassword("jadslkföa");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createAndReturnUser(testUser2));
    }
}
