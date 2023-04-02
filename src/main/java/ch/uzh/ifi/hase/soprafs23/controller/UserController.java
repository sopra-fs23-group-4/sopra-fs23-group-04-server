package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
    this.userService = userService;
    }
    Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);

    response.addHeader("Authorization", createdUser.getToken());
    log.info("The user " + createdUser.getUsername()+ " with id "+ createdUser.getId()+ " has been created.");
    // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

@PostMapping("/login")
@ResponseStatus(HttpStatus.CREATED)
@ResponseBody
public UserGetDTO logInUser(@RequestBody UserLoginDTO userLoginDTO,HttpServletResponse response){
    User userCredentials =DTOMapper.INSTANCE.convertUserLoginPostDTOtoEntity(userLoginDTO);

    User user =userService.logIn(userCredentials);

    response.addHeader("Authorization", user.getToken());
    response.addHeader("Authorization", user.getToken());

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }
    
@PutMapping("/users/{userId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void editUser(@PathVariable("userId") Long userId, @RequestBody UserPutDTO userPutDTO){

    User userCredentials = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

    userService.editUser(userId, userCredentials);

}

}
