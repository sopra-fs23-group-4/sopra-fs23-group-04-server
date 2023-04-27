package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.*;
import ch.uzh.ifi.hase.soprafs23.repository.*;
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
import static ch.uzh.ifi.hase.soprafs23.constant.RoundStatus.FINISHED;

@Service
@Transactional
public class AnswerService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final RoundRepository roundRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AnswerService(@Qualifier("gameRepository") GameRepository gameRepository,
                         @Qualifier("userRepository") UserRepository userRepository,
                         @Qualifier("roundRepository") RoundRepository roundRepository,
                         @Qualifier("answerRepository") AnswerRepository answerRepository,
                         @Qualifier("categoryRepository") CategoryRepository categoryRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.roundRepository = roundRepository;
        this.answerRepository = answerRepository;
        this.categoryRepository = categoryRepository;
    }

    public void saveAnswers(int gamePin, String userToken, int roundNumber, Map<String, String> answers) {

        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExists(game);
        checkIfGameIsOpen(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);

        checkIfUserIsInGame(game, user);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExists(round);
        checkIfRoundIsFinished(round);

        checkIfAnswersAlreadyExist(round, user);

        saveAnswersToDatabase(answers, user, round);
    }

    public List<Map<Long, String>> getAnswers(int gamePin, int roundNumber, String categoryName, String userToken) {
        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExists(game);
        checkIfGameIsOpen(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);

        checkIfUserIsInGame(game, user);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExists(round);
        checkIfRoundIsFinished(round);

        Category category = getCategory(categoryName);
        checkIfCategoryExists(category);

        List<Answer> answers = answerRepository.findByRoundAndCategory(round, category);

        return filterAnswersByDeletingUser(answers, user);
    }

    /**
     * Helper methods to aid with the answer saving, creation and retrieval
     */

    private void checkIfGameExists(Game game) {
        String errorMessage = "Game does not exist. Please try again with a different game!";

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    private void checkIfGameIsOpen(Game game) {
        String errorMessage = "Game is not open anymore. Please try again with a different game!";

        if (!game.getStatus().equals(OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    private void checkIfUserExists(User user) {

        String errorMessage = "User does not exist." +
                "Please register before playing!";
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }

    private void checkIfUserIsInGame(Game game, User user) {
        List<User> users = game.getUsers();

        String errorMessage = "User is not part of this game.";

        if(!users.contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }

    private void checkIfRoundExists(Round round) {
        String errorMessage = "Round does not exist.";

        if (round == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    private void checkIfRoundIsFinished(Round round) {
        String errorMessage = "Round is not finished yet.";

        if (!round.getStatus().equals(FINISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    private void checkIfAnswersAlreadyExist(Round round, User user) {

        List<Answer> answers = answerRepository.findByRoundAndUser(round, user);

        String errorMessage = "These Answers have already been saved.";

        if (answers.size() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private Category getCategory(String categoryName) {

        Category category = categoryRepository.findByName(categoryName);

        return category;
    }

    private void checkIfCategoryExists(Category category) {

        String errorMessage = "There exists no such category.";

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }

    private void saveAnswersToDatabase(Map<String, String> answers, User user, Round round) {
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String categoryName = entry.getKey();
            Category category = getCategory(categoryName);
            String answer = entry.getValue();
            Answer newAnswer = new Answer();
            newAnswer.setRound(round);
            newAnswer.setUser(user);
            newAnswer.setAnswerString(answer);
            newAnswer.setCategory(category);
            newAnswer = answerRepository.save(newAnswer);
        }
        answerRepository.flush();
    }

    private List<Map<Long, String>> filterAnswersByDeletingUser(List<Answer> answers, User user) {

        List<Map<Long, String>> filteredAnswers = new ArrayList<>();

        for (Answer answer : answers) {
            if (!answer.getUser().equals(user)) {
                Map<Long, String> answerTuple = new HashMap<>();
                answerTuple.put(answer.getAnswerId(), answer.getAnswerString());
                filteredAnswers.add(answerTuple);
            }
        }
        return filteredAnswers;
    }
}
