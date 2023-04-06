package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("username");
        testUser.setPassword("password");
        testUser.setToken("valid-token");

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

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User editedUser = new User();
        editedUser.setId(1L);
        editedUser.setUsername("new-username");
        editedUser.setPassword("new-password");
        editedUser.setToken("valid-token");

        // When
        User resultUser = userService.editUser(1L, editedUser);

        // Then
        assertEquals(editedUser.getId(), resultUser.getId());
        assertEquals(editedUser.getUsername(), resultUser.getUsername());
        assertEquals(editedUser.getPassword(), resultUser.getPassword());
        assertEquals(editedUser.getToken(), resultUser.getToken());
    }

    @Test
    public void editUser_userNotFound_throwResponseStatusException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User editedUser = new User();
        editedUser.setId(1L);
        editedUser.setUsername("new-username");
        editedUser.setPassword("new-password");
        editedUser.setToken("valid-token");

        // Then (expect exception)
        assertThrows(ResponseStatusException.class, () -> userService.editUser(1L, editedUser));
    }

    @Test
    public void editUser_invalidToken_throwResponseStatusException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User editedUser = new User();
        editedUser.setId(1L);
        editedUser.setUsername("new-username");
        editedUser.setPassword("new-password");
        editedUser.setToken("invalid-token");

        // Then (expect exception)
        assertThrows(ResponseStatusException.class, () -> userService.editUser(1L, editedUser));
    }

}
