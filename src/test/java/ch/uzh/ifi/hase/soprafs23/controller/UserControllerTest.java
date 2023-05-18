package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


  @Test
  void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");

    user.setQuote("My penis was in the Guinness book of records!" +
              "\n" +
              "Until the librarian told me to take it out.");

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is(user.getUsername())));
    }

  @Test
  void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1);
    user.setUsername("testUsername");
    user.setToken("1");
    user.setQuote("My penis was in the Guinness book of records!" +
              "\n" +
              "Until the librarian told me to take it out.");


    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

        given(userService.createAndReturnUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(user.getId())))
            .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    void logInUser_validInput_userLoggedIn() throws Exception {
        // given
        User user = new User();
        user.setId(1);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setQuote("My penis was in the Guinness book of records!" +
                "\n" +
                "Until the librarian told me to take it out.");

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("testUsername");
        userLoginDTO.setPassword("testPassword");

        given(userService.logIn(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLoginDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(header().string("Authorization", user.getToken()));
    }

    /*@Test
    void editUser_validInput_userEdit_Quote () throws Exception {
        // given
        User user = new User();
        user.setId(1);
        user.setUsername("testUsername");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setQuote("I am the best");


        given(userService.editUserQuote(Mockito.anyInt(), Mockito.any(), Mockito.anyString())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }
*/
    @Test
    void logInUser_invalidInput_userNotLoggedIn() throws Exception {
        // given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("invalidUsername");
        userLoginDTO.setPassword("invalidPassword");

        // tell the method to throw an exception when userService.logIn() is called with invalid input
        given(userService.logIn(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // when
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLoginDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }



    /*@Test
    void editUser_validInput_userEdited () throws Exception {
        // given
        User user = new User();
        user.setQuote("I am the best");

        UserPutDTO userPutDTO = new UserPutDTO();

        userPutDTO.setQuote("I am the best");

        given(userService.editUserQuote(Mockito.anyInt(),Mockito.any(), Mockito.anyString())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }*/

    @Test
    void editUser_invalidInput_userEdited () throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setQuote("qoute");

        // tell the method to do nothing when userService.editUser() is called

        given(userService.editUserQuote(Mockito.anyInt(),Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }
    @Test
    void getUserByID_correctInput_returnUser() throws Exception {
        // given
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setUsername("testUsername");

        given(userService.getUserById(userId)).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }
    @Test
    void getUserByUsername_correctInput_returnUser() throws Exception {
        // given
        String username = "testUsername";
        User user = new User();
        user.setId(1);
        user.setUsername(username);

        given(userService.getUserByUsername(username)).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/username/{username}", username)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    void getUserByUsername_incorrectInput_userNotFound() throws Exception {
        // given
        String username = "nonExistingUsername";

        given(userService.getUserByUsername(username)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/username/{username}", username)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }




    /**
    * Helper Method to convert userPostDTO into a JSON string such that the input
    * can be processed
    * Input will look like this: {"name": "Test User", "username": "testUsername"}
    *
    * @param object
    * @return string
    */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              String.format("The request body could not be created.%s", e.toString()));
        }
    }
}