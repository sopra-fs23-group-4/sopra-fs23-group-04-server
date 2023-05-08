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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnswerServiceIntegrationTest {

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

    private User user;
    private Game game;
    private Round round;

    @BeforeEach
    public void setUp() {
        // Create and save a user
        User userForCreation = new User();
        userForCreation.setUsername("test1");
        userForCreation.setPassword("testPassword");
        user = userService.createAndReturnUser(userForCreation);

        // Create and save a game
        Game gameForCreation = new Game();
        gameForCreation.setRounds(10);
        gameForCreation.setRoundLength(RoundLength.MEDIUM);
        gameForCreation.setCategories(getCategories());
        game = gameService.createAndReturnGame(gameForCreation, user.getToken());

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
        assertDoesNotThrow(() -> answerService.saveAnswers(game.getGamePin(), user.getToken(), 1, answers));

        List<Answer> savedAnswers = answerRepository.findByRoundAndUser(round, user);

        // Verify that the saved answers match the provided answers
        assertEquals(answers.size(), savedAnswers.size());
        for (Answer savedAnswer : savedAnswers) {
            String categoryName = savedAnswer.getCategory().getName();
            String submittedAnswer = answers.get(categoryName);
            assertEquals(submittedAnswer, savedAnswer.getAnswerString());
        }
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
