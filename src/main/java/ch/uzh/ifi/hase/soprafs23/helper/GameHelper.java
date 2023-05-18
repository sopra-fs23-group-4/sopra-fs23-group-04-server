package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.RUNNING;

public class GameHelper {

    private GameHelper() {}

    public static void checkIfGameExists(Game game) {

        String errorMessage = "Game does not exist. Please try again with a different game!";

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public static void checkIfGameIsOpen(Game game) {

        String errorMessage = "Game is not open. Please try again with a different pin!";

        if (game.getStatus().equals(GameStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

    }

    public static void checkIfGameIsRunning(Game game) {
        String errorMessage = "Game is not running. Please try again with a different game!";

        if (!game.getStatus().equals(RUNNING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    public static void checkIfUserIsInGame(Game game, User user) {
        List<User> users = game.getActiveUsers();

        String errorMessage = "User is not part of this game.";

        if(!users.contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }

    public static void checkIfNotToManyCategories(Game game){
        List<Category> gameCategories = game.getCategories();
        if (gameCategories.size()>10) {
            String errorMessage ="You're game cannot have more than ten categories";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,errorMessage);
        }
    }

    public static void checkCategoryNames(Game game){
        List<Category> gameCategories = game.getCategories();
        for (Category category: gameCategories) {
            if (category.getName().length()> Constant.MAX_STRING_LENGTH_OF_CATEGORY) {
                String errorMessage ="One of the categories you selected is too loonnnggg";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,errorMessage);
            }
        }
    }
    public static Boolean checkIfUserIsHost(User user, Game game) {
        int hostId = game.getHostId();

        return hostId == user.getId();
    }

    public static Boolean checkIfGameHasUsers(Game game) {
        List<User> users = game.getActiveUsers();
        return !users.isEmpty();
    }

    public static List<Character> generateRandomLetters(int numberOfRounds){
        List<Character> letters = new ArrayList<>();

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            letters.add(letter);
        }

        Collections.shuffle(letters);

        return letters.subList(0, numberOfRounds);
    }

    public static GameCategoriesDTO getStandardCategories() {

        GameCategoriesDTO gameCategoriesDTO = new GameCategoriesDTO();
        gameCategoriesDTO.setCategories(GameCategory.getCategories());

        return gameCategoriesDTO;
    }

    public static List<String> getCategoryNamesByGame(Game game) {
        List<Category> gameCategories = game.getCategories();

        List<String> gameCategoryNames = new ArrayList<>();

        for (Category gameCategory : gameCategories) {
            gameCategoryNames.add(gameCategory.getName());
        }
        return gameCategoryNames;
    }

     public static List<Integer> getGameUsersId(Game game) {
        List<Integer> usersId = new ArrayList<>();
        for (User user : game.getActiveUsers()) {
            usersId.add(user.getId());
        }
        return usersId;
    }
}
