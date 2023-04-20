package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.OPEN;
import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.RUNNING;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public int createGame(Game newGame, String userToken) {

        User user = getUserByToken(userToken);

        checkIfUserExists(user);

        checkIfHostIsEligible(user.getId());

        newGame.setGamePin(generateGamePin());
        newGame.setStatus(OPEN);
        newGame.setHostId(user.getId());
        newGame.addPlayer(user);
        newGame.setRoundLetters(generateRandomLetters(newGame.getRounds()));

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created following game: {}", newGame);
        return newGame.getGamePin();
    }

    public List<Character> generateRandomLetters(Long numberOfRounds){
        List<Character> letters = new ArrayList<>();

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            letters.add(letter);
        }

        Collections.shuffle(letters);

        return letters.subList(0, numberOfRounds.intValue());
    }

    public void joinGame(int gamePin, String userToken) {

        User user = getUserByToken(userToken);

        checkIfUserExists(user);

        checkIfUserCanJoin(user.getId());

        Game gameToJoin = gameRepository.findByGamePin(gamePin);

        checkIfGameExists(gameToJoin);

        gameToJoin.addPlayer(user);
    }

    public Game getGameByGameId(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);
        String errorMessage = "Game does not exist!";
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
        return game;
    }

    /**
     * Helper methods to aid in the game creation, modification and deletion
     */

    private List<Game> getOpenGames() {
        return gameRepository.findByStatus(OPEN);
    }

    private List<Game> getRunningGames() {
        return gameRepository.findByStatus(RUNNING);
    }

    private List<Game> getOpenOrRunningGames() {
        List<Game> openOrRunningGames = getOpenGames();
        openOrRunningGames.addAll(getRunningGames());
        return openOrRunningGames;
    }

    private List<Long> getGameUsersId(Game game) {
        List<Long> usersId = new ArrayList<>();
        for (User user : game.getUsers()) {
            usersId.add(user.getId());
        }
        return usersId;
    }

    private void checkIfHostIsEligible(Long hostId) {
        List<Game> openOrRunningGames = getOpenOrRunningGames();

        String errorMessage = "You are already part of a game." +
                "You cannot host another game!";
        for (Game game : openOrRunningGames) {
            List<Long> userIds = getGameUsersId(game);
            if (userIds.contains(hostId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format(errorMessage));
            }
        }
    }

    private void checkIfUserCanJoin(Long userId) {

        List<Game> openOrRunningGames = getOpenOrRunningGames();

        String errorMessage = "You are already part of a game." +
                "You cannot join another game!";

        for (Game game : openOrRunningGames) {
            List<Long> userIds = getGameUsersId(game);
            if (userIds.contains(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format(errorMessage));
            }
        }
    }

    private void checkIfUserExists(User user) {

        String errorMessage = "User does not exist." +
                "Please register before playing!";
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfGameExists(Game game) {

        String errorMessage = "Game does not exist or is not open anymore." +
                "Please try again with a different pin!";
        if (game == null || !game.getStatus().equals(OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private User getUserByToken(String userToken) {
        return userRepository.findByToken(userToken);
    }

    private int generateGamePin() {

        Random rnd = new Random();

        return rnd.nextInt(9000) + 1000;
    }
}
