package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserHelper {

    public void checkIfUserExists(User user) {

        String errorMessage = "User does not exist. " +
                "Please register before playing!";

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }
}
