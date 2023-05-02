package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock // Add this annotation to mock the QuoteService
    private QuoteService quoteService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("username");
        testUser.setPassword("password");
        testUser.setToken("valid-token");
        testUser.setQuote("defaultQuote");

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        User createdUser = userService.createUser(testUser);

        // then
        verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());

        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void editUser_validInputs_success() {

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        User editedUser = new User();
        editedUser.setId(1);
        editedUser.setUsername("new-username");
        editedUser.setPassword("new-password");
        editedUser.setToken("valid-token");
        editedUser.setQuote("new-quote"); // Add a new quote for the edited user

        // When
        User resultUser = userService.editUser(1, editedUser);

        // Then
        assertEquals(editedUser.getId(), resultUser.getId());
        assertEquals(editedUser.getUsername(), resultUser.getUsername());
        assertEquals(editedUser.getPassword(), resultUser.getPassword());
        assertEquals(editedUser.getToken(), resultUser.getToken());
        assertEquals(editedUser.getQuote(), resultUser.getQuote()); // Verify that the saved user has the new quote
    }

    @Test
    public void editUser_userNotFound_throwResponseStatusException() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        User editedUser = new User();
        editedUser.setId(1);
        editedUser.setUsername("new-username");
        editedUser.setPassword("new-password");
        editedUser.setToken("valid-token");

        // Then (expect exception)
        assertThrows(ResponseStatusException.class, () -> userService.editUser(1, editedUser));
    }

    @Test
    public void editUser_invalidToken_throwResponseStatusException() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        User editedUser = new User();
        editedUser.setId(1);
        editedUser.setUsername("new-username");
        editedUser.setPassword("new-password");
        editedUser.setToken("invalid-token");

        // Then (expect exception)
        assertThrows(ResponseStatusException.class, () -> userService.editUser(1, editedUser));
    }

    @Test
    public void logIn_validInputs_success() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(testUser);

        // When
        User loggedInUser = userService.logIn(testUser);

        // Then
        assertEquals(testUser.getId(), loggedInUser.getId());
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    }

    @Test
    public void logIn_usernameDoesNotExist_throwsException() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        // Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.logIn(testUser));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getReason().contains("This username doesn't exist"));
    }

    @Test
    public void logIn_incorrectPassword_throwsException() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(testUser);

        // Create a user object with incorrect password
        User userWithIncorrectPassword = new User();
        userWithIncorrectPassword.setUsername(testUser.getUsername());
        userWithIncorrectPassword.setPassword("incorrect-password");

        // Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.logIn(userWithIncorrectPassword));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertTrue(exception.getReason().contains("The password you tipped in is incorrect"));
    }
    @Test
    public void logout_success() {
        // create User
        User user = new User();
        user.setToken("token");
        user.setStatus(UserStatus.ONLINE);


        when(userRepository.findByToken(user.getToken())).thenReturn(user);


        userService.logout(user);

        assertEquals(UserStatus.OFFLINE, user.getStatus());
    }

    @Test
    public void logout_failed(){
        User user = new User();
        user.setToken("token");
        user.setStatus(UserStatus.ONLINE);


        when(userRepository.findByToken(user.getToken())).thenReturn(null);
        
        assertThrows(ResponseStatusException.class, () -> userService.logout(user));

    }

    @Test
    public void getUsers_usersExistInRepository_success() {
        // Given
        User anotherTestUser = new User();
        anotherTestUser.setId(2);
        anotherTestUser.setUsername("anotherUsername");
        anotherTestUser.setPassword("anotherPassword");

        List<User> expectedUsers = Arrays.asList(testUser, anotherTestUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = userService.getUsers();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void checkIfUsernameValid_validUsername_noExceptionThrown() {
        // Given
        User validUser = new User();
        validUser.setUsername("validUsername");

        // When
        userService.checkIfUsernameValid(validUser);

        // Then
        // No exception should be thrown, so no need to add any assertions
    }

    @Test
    public void checkIfUsernameValid_invalidUsername_throwsException() {
        // Given
        User invalidUser = new User();
        invalidUser.setUsername("invalid username"); // Contains space

        // Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.checkIfUsernameValid(invalidUser));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getReason().contains("The username provided is not valid. Please choose a single word!"));
    }

    @Test
    public void getUserById_validId_returnsUser() {
        // Given
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User retrievedUser = userService.getUserById(userId);

        // Then
        assertNotNull(retrievedUser);
        assertEquals(testUser.getId(), retrievedUser.getId());
        assertEquals(testUser.getUsername(), retrievedUser.getUsername());
    }

    @Test
    public void getUserById_invalidId_throwsResponseStatusException() {
        // Given
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.getUserById(userId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("User not found"));
    }

    @Test
    public void getUserByUsername_validUsername_returnsUser() {
        // Given
        String username = "username";
        when(userRepository.findByUsername(username)).thenReturn(testUser);

        // When
        User retrievedUser = userService.getUserByUsername(username);

        // Then
        assertNotNull(retrievedUser);
        assertEquals(testUser.getId(), retrievedUser.getId());
        assertEquals(testUser.getUsername(), retrievedUser.getUsername());
    }

    @Test
    public void getUserByUsername_invalidUsername_throwsResponseStatusException() {
        // Given
        String username = "nonExistingUsername";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.getUserByUsername(username));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("User not found"));
    }

}
