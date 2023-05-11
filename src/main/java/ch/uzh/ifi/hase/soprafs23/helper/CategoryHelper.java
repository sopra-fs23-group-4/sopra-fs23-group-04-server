package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CategoryHelper {

    private CategoryHelper() {}

    public static void checkIfCategoryExists(Category category) {

        String errorMessage = "This category does not exist.";

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }

}
