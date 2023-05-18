package ch.uzh.ifi.hase.soprafs23.serviceIntegration;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnswerServiceIntegrationTest {

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
    private User user3;
    private Game game;
    private Round round;

    @BeforeEach
    void setUp() {
        user1 = createUserForTesting();

        user2 = createUserForTesting();

        user3 = createUserForTesting();

        game = createGameForTesting(user1.getToken());

        game.addPlayer(user2);

        round = roundRepository.findByGameAndRoundNumber(game, 1);
    }

    @Test
    void saveAnswers_validInput_answersSaved() {

        Map<String, String> answers = new HashMap<>();
        answers.put("Stadt", "Athen");
        answers.put("Land", "Armenien");
        answers.put("Auto", "Audi");
        answers.put("Film Regisseur", null);

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        int gamePin = game.getGamePin();
        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user1Token, 1, answers));

        List<Answer> savedAnswers = answerRepository.findByRoundAndUser(round, user1);

        assertEquals(answers.size(), savedAnswers.size());

        for (Answer savedAnswer : savedAnswers) {
            String categoryName = savedAnswer.getCategory().getName();
            String submittedAnswer = answers.get(categoryName);
            assertEquals(Objects.requireNonNullElse(submittedAnswer, "-"), savedAnswer.getAnswerString());
        }
    }

    @Test
    void saveAnswers_validInput_savedTwice() {

        Map<String, String> answers = Map.of(
                "Stadt", "Athen",
                "Land", "Armenien",
                "Auto", "Audi",
                "Film Regisseur", "Woody Allen");

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        int gamePin = game.getGamePin();
        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user1Token, 1, answers));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> answerService.saveAnswers(gamePin, user1Token, 1, answers));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("These Answers have already been saved.", exception.getReason());
    }

    @Test
    void saveAnswers_userNotInGame() {

        Map<String, String> answers = Map.of(
                "Stadt", "Athen",
                "Land", "Armenien",
                "Auto", "Audi",
                "Film Regisseur", "Woody Allen");

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        int gamePin = game.getGamePin();
        String user3Token = user3.getToken();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> answerService.saveAnswers(gamePin, user3Token, 1, answers));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("User is not part of this game.", exception.getReason());

    }

    @Test
    void getAnswers_validInput() {

        Map<String, String> answersUser1And2 = Map.of(
                "Stadt", "Athen",
                "Land", "Armenien",
                "Auto", "Audi",
                "Film Regisseur", "Woody Allen");

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        assertDoesNotThrow(() -> answerService.saveAnswers(game.getGamePin(), user1.getToken(), 1, answersUser1And2));
        assertDoesNotThrow(() -> answerService.saveAnswers(game.getGamePin(), user2.getToken(), 1, answersUser1And2));

        Map<Integer, String> answerUser1Stadt = new HashMap<>();
        answerUser1Stadt.put(1, "Athen");

        List<Map<Integer, String>> answerListExpected = new ArrayList<>();
        answerListExpected.add(answerUser1Stadt);

        List<Map<Integer, String>> answerListActual =
                answerService.getAnswers(game.getGamePin(), 1, "Stadt", user2.getToken());

        assertEquals(answerListExpected.size(), answerListActual.size());

        Map<Integer, String> expectedMap = answerListExpected.get(0);
        Map<Integer, String> actualMap = answerListActual.get(0);

        assertEquals(expectedMap.values().iterator().next(), actualMap.values().iterator().next());
    }

    private int userNameSuffix = 1;
    private User createUserForTesting() {
        User userForCreation = new User();

        String userName = String.format("user%d", userNameSuffix);
        userNameSuffix++;

        userForCreation.setUsername(userName);
        userForCreation.setPassword("testPassword");

        return userService.createAndReturnUser(userForCreation);
    }

    private Game createGameForTesting(String userToken) {

        Game gameForCreation = new Game();
        gameForCreation.setRounds(10);
        gameForCreation.setRoundLength(RoundLength.MEDIUM);
        gameForCreation.setCategories(getCategories());

        return gameService.createAndReturnGame(gameForCreation, userToken);
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
