package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    int token = 1;

    public synchronized User createAndReturnUser(User newUser) {
        // TODO change setToken back to random
        //newUser.setToken(UUID.randomUUID().toString());
        if (newUser.getUsername().length()>10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You're username exceeds 10 character");
        }
        newUser.setToken(Integer.toString(token));
        token++;
        newUser.setStatus(UserStatus.ONLINE);
        checkIfUsernameAlreadyExists(newUser);
        checkIfUsernameValid(newUser);
        newUser.setCreationDate((LocalDate.now()));
        newUser.setQuote("No Quote");

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        logger.debug("Created Information for User: {}", newUser);
        return newUser;

    }

    public User logIn(User userLogin){
        User userByUsername = userRepository.findByUsername(userLogin.getUsername());
        String notExist = "This username doesn't exist";
        String wrongPassword = "The password you tipped in is incorrect";

        if (userByUsername == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,notExist);
        }
        if (!Objects.equals(userLogin.getPassword(), userByUsername.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,wrongPassword);
        }

        userByUsername.setStatus(UserStatus.ONLINE);
        return userByUsername;

    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    public synchronized User editUser (int userId, User editedUser) {

        User userDB = userRepository.findById(userId).orElse(null);

        String notExist = "The user doesn't exist!";
        String wrongPassword = "You are not authorized to edit this profile!";

        if (userDB == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notExist);
        }
        if (!userDB.getToken().equals(editedUser.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,wrongPassword);
        }
        changeProfile(userDB, editedUser);

        return userDB;
    }

    /**
    * This is a helper method that will check the uniqueness criteria of the
    * username and the name
    * defined in the User entity. The method will do nothing if the input is unique
    * and throw an error otherwise.
    *
    * @param userToBeCreated
    * @throws org.springframework.web.server.ResponseStatusException
    * @see User
    */
    private void checkIfUsernameAlreadyExists(User userToBeCreated) {

        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The username provided is not unique. Therefore, the user could not be created or updated!";

        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage));
        }
    }

    void checkIfUsernameValid(User userToBeCreated) {
        String baseErrorMessage = "The username provided is not valid. Please choose a single word!";
        if (userToBeCreated.getUsername().contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage));
        }
    }

    private void changeProfile(User userDB, User editedUser) {
        String newUsername = editedUser.getUsername();
        String newPassword = editedUser.getPassword();
        String newQuote = editedUser.getQuote();
        // Byte[] newPic = editedUser.getP
        if (!Objects.equals(newUsername, null)) {
            checkIfUsernameAlreadyExists(editedUser);
            checkIfUsernameValid(editedUser);
            userDB.setUsername(newUsername);
        }
        if (!Objects.equals(newQuote, null)) {
            checkIfQuoteValid(newQuote);
            userDB.setQuote(newQuote);
        }
        if (!Objects.equals(newPassword, null)) {
            userDB.setPassword(newPassword);
        }
        userRepository.save(userDB);
    }

    public void logout(User user){
        User foundUser = userRepository.findByToken(user.getToken());
        if (foundUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        foundUser.setStatus(UserStatus.OFFLINE);
    }

    private void checkIfQuoteValid(String quote){
        if (quote.length()>255){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The quote you are trying to add is to long");
        }
    }

}
