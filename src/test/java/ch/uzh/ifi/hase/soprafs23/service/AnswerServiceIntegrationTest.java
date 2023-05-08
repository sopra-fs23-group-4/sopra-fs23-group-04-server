package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnswerServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;

    private User user1;
    private User user2;
    private Game game;
    private Round round;

    @BeforeEach
    public void setUp() {
        // Create and save a user
        User userForCreation = new User();
        userForCreation.setUsername("user1");
        userForCreation.setPassword("testPassword");
        user1 = userService.createAndReturnUser(userForCreation);

        User userForCreation2 = new User();
        userForCreation2.setUsername("user2");
        userForCreation2.setPassword("testPassword");
        user2 = userService.createAndReturnUser(userForCreation2);

        // Create and save a game
        Game gameForCreation = new Game();
        gameForCreation.setRounds(10);
        gameForCreation.setRoundLength(RoundLength.MEDIUM);
        gameForCreation.setCategories(getCategories());
        game = gameService.createAndReturnGame(gameForCreation, user1.getToken());
        game.addPlayer(user2);

        round = roundRepository.findByGameAndRoundNumber(game, 1);

    }

    @Test
    public void saveAnswers_validInput_answersSaved() {

        Map<String, String> answers = Map.of(
                "Stadt", "Athen",
                "Land", "Armenien",
                "Auto", "Audi",
                "Film Regisseur", "Woody Allen");

        // change status of game to RUNNING
        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        // change status of round to FINISHED
        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        // No exception should be thrown in this case
        assertDoesNotThrow(() -> answerService.saveAnswers(game.getGamePin(), user1.getToken(), 1, answers));

        List<Answer> savedAnswers = answerRepository.findByRoundAndUser(round, user1);

        // Verify that the saved answers match the provided answers
        assertEquals(answers.size(), savedAnswers.size());
        for (Answer savedAnswer : savedAnswers) {
            String categoryName = savedAnswer.getCategory().getName();
            String submittedAnswer = answers.get(categoryName);
            assertEquals(submittedAnswer, savedAnswer.getAnswerString());
        }
    }

    @Test
    public void getAnswers_validInput() {

        Map<String, String> answersUser1 = Map.of(
                "Stadt", "Athen",
                "Land", "Armenien",
                "Auto", "Audi",
                "Film Regisseur", "Woody Allen");

        Map<String, String> answersUser2 = Map.of(
                "Stadt", "Athen",
                "Land", "Armenien",
                "Auto", "Audi",
                "Film Regisseur", "Woody Allen");

        // change status of game to RUNNING
        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        // change status of round to FINISHED
        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        // No exception should be thrown in this case
        assertDoesNotThrow(() -> answerService.saveAnswers(game.getGamePin(), user1.getToken(), 1, answersUser1));
        assertDoesNotThrow(() -> answerService.saveAnswers(game.getGamePin(), user2.getToken(), 1, answersUser2));

        Map<Integer, String> answerUser1Stadt = new HashMap<>();
        answerUser1Stadt.put(1, "Athen");

        List<Map<Integer, String>> answerListExpected = new ArrayList<>();
        answerListExpected.add(answerUser1Stadt);

        List<Map<Integer, String>> answerListActual =
                answerService.getAnswers(game.getGamePin(), 1, "Stadt", user2.getToken());

        assertEquals(answerListExpected, answerListActual);
    }

    private List<Category> getCategories() {

        List<String> categoryNames = Arrays.asList("Stadt", "Land", "Auto", "Film Regisseur");

        List<Category> mappedCategories = new ArrayList<>();

        for(String categoryName : categoryNames) {
            Category mappedCategory = new Category();
            mappedCategory.setName(categoryName);
            mappedCategories.add(mappedCategory);
        }
        return mappedCategories;
    }
}
