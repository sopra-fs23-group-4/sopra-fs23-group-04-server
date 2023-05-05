package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.*;
import ch.uzh.ifi.hase.soprafs23.helper.GameHelper;
import ch.uzh.ifi.hase.soprafs23.helper.UserHelper;
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

import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.*;
import static ch.uzh.ifi.hase.soprafs23.constant.RoundStatus.FINISHED;
import static ch.uzh.ifi.hase.soprafs23.constant.ScorePoint.INCORRECT;

@Service
@Transactional
public class AnswerService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final RoundRepository roundRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;
    private final GameHelper gameHelper;
    private final UserHelper userHelper;

    @Autowired
    public AnswerService(@Qualifier("gameRepository") GameRepository gameRepository,
                         @Qualifier("userRepository") UserRepository userRepository,
                         @Qualifier("roundRepository") RoundRepository roundRepository,
                         @Qualifier("answerRepository") AnswerRepository answerRepository,
                         @Qualifier("categoryRepository") CategoryRepository categoryRepository,
                         GameHelper gameHelper,
                         UserHelper userHelper) {

        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.roundRepository = roundRepository;
        this.answerRepository = answerRepository;
        this.categoryRepository = categoryRepository;

        this.gameHelper = gameHelper;
        this.userHelper = userHelper;
    }

    public void saveAnswers(int gamePin, String userToken, int roundNumber, Map<String, String> answers) {

        Game game = gameRepository.findByGamePin(gamePin);
        gameHelper.checkIfGameExists(game);
        gameHelper.checkIfGameIsRunning(game);

        User user = userRepository.findByToken(userToken);
        userHelper.checkIfUserExists(user);

        gameHelper.checkIfUserIsInGame(game, user);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExists(round);
        checkIfRoundIsFinished(round);

        checkIfAnswersAlreadyExist(round, user);

        saveAnswersToDatabase(answers, user, round);
    }

    public List<Map<Integer, String>> getAnswers(int gamePin, int roundNumber, String categoryName, String userToken) {
        Game game = gameRepository.findByGamePin(gamePin);
        gameHelper.checkIfGameExists(game);
        gameHelper.checkIfGameIsRunning(game);

        User user = userRepository.findByToken(userToken);
        userHelper.checkIfUserExists(user);

        gameHelper.checkIfUserIsInGame(game, user);

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

        return categoryRepository.findByName(categoryName);
    }

    private void checkIfCategoryExists(Category category) {

        String errorMessage = "There exists no such category.";

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }

    private void saveAnswersToDatabase(Map<String, String> answers, User user, Round round) {
        for (Map.Entry<String, String> answer : answers.entrySet()) {

            String categoryName = answer.getKey();
            Category category = getCategory(categoryName);
            String answerString = answer.getValue();

            if (answerString == null) {
                answerString = "";
            }

            Answer newAnswer = new Answer();

            newAnswer.setRound(round);
            newAnswer.setUser(user);
            newAnswer.setAnswerString(answerString);
            newAnswer.setCategory(category);
            newAnswer.setScorePoint(INCORRECT);

            answerRepository.save(newAnswer);
        }
        answerRepository.flush();
    }

    private List<Map<Integer, String>> filterAnswersByDeletingUser(List<Answer> answers, User user) {

        List<Map<Integer, String>> filteredAnswers = new ArrayList<>();

        for (Answer answer : answers) {
            if (!answer.getUser().equals(user)) {
                Map<Integer, String> answerTuple = new HashMap<>();
                answerTuple.put(answer.getAnswerId(), answer.getAnswerString());
                filteredAnswers.add(answerTuple);
            }
        }
        return filteredAnswers;
    }
}
