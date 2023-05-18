package ch.uzh.ifi.hase.soprafs23.serviceIntegration;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.AdvancedStatisticGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdvancedStatisticServiceIntegrationTest {

    @Autowired
    private AdvancedStatisticService advancedStatisticService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private VoteService voteService;
    @Autowired
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Game game;
    private final List<String> categoryNames = List.of("Stadt");

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        roundRepository.deleteAll();
        answerRepository.deleteAll();

        user1 = createUserForTesting();
        user2 = createUserForTesting();
        user3 = createUserForTesting();
        user4 = createUserForTesting();

        game = createGameForTesting();

    }

    @Test
    void getAdvancedUserStatistic_validInput() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();
        String user3Token = user3.getToken();
        String user4Token = user4.getToken();

        Map<String, String> answers = getCategoryAnswerMap();
        game = gameService.createAndReturnGame(game, user1Token);
        int gamePin = game.getGamePin();

        gameService.joinGame(gamePin, user2Token);
        gameService.joinGame(gamePin, user3Token);
        gameService.joinGame(gamePin, user4Token);

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        Round round = roundRepository.findByGameAndRoundNumber(game, 1);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        answerService.saveAnswers(gamePin, user1Token, 1, answers);
        answerService.saveAnswers(gamePin, user2Token, 1, answers);
        answerService.saveAnswers(gamePin, user3Token, 1, answers);
        answerService.saveAnswers(gamePin, user4Token, 1, answers);

        Map<Integer, String> votingOfUser1 = Map.of(
                2, "WRONG",
                3, "CORRECT_NOT_UNIQUE",
                4, "WRONG"
        );

        Map<Integer, String> votingOfUser2 = new HashMap<>();
        votingOfUser2.put(1, null);
        votingOfUser2.put(3, null);
        votingOfUser2.put(4, null);

        Map<Integer, String> votingOfUser3 = Map.of(
                1, "CORRECT_UNIQUE",
                2, "CORRECT_NOT_UNIQUE",
                4, "WRONG"
        );

        Map<Integer, String> votingOfUser4 = Map.of(
                1, "WRONG",
                2, "CORRECT_NOT_UNIQUE",
                3, "CORRECT_NOT_UNIQUE"
        );

        voteService.saveVote(gamePin, categoryNames.get(0), user1Token, votingOfUser1);
        voteService.saveVote(gamePin, categoryNames.get(0), user2Token, votingOfUser2);
        voteService.saveVote(gamePin, categoryNames.get(0), user3Token, votingOfUser3);
        voteService.saveVote(gamePin, categoryNames.get(0), user4Token, votingOfUser4);

        String categoryName = categoryNames.get(0);

        AtomicReference<AdvancedStatisticGetDTO> advancedStatisticGetDTO = new AtomicReference<>();
        assertDoesNotThrow(() -> advancedStatisticGetDTO.set(advancedStatisticService.getAdvancedUserStatistic(user1.getId())));

        assertEquals(1, advancedStatisticGetDTO.get().getRank());
        assertEquals(1, advancedStatisticGetDTO.get().getTotalWins());
        assertEquals(1, advancedStatisticGetDTO.get().getTotalPlayedGames());
        assertEquals(1, advancedStatisticGetDTO.get().getTotalAnswersAnswered());
        assertEquals(1, advancedStatisticGetDTO.get().getTotalCorrectAndUniqueAnswers());
        assertEquals(categoryName, advancedStatisticGetDTO.get().getMostPlayedCategory());

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

    private Game createGameForTesting() {

        Game gameForCreation = new Game();
        gameForCreation.setRounds(1);
        gameForCreation.setRoundLength(RoundLength.MEDIUM);
        gameForCreation.setCategories(getCategories());

        return gameForCreation;
    }

    private List<Category> getCategories() {

        List<Category> mappedCategories = new ArrayList<>();

        for (String categoryName : categoryNames) {
            Category mappedCategory = new Category();
            mappedCategory.setName(categoryName);
            mappedCategories.add(mappedCategory);
        }
        return mappedCategories;
    }

    private Map<String, String> getCategoryAnswerMap() {
        return Map.of(
                "Stadt", "Athen");
    }

    private Map<String, String> getCategoryEmptyAnswerMap() {
        Map<String, String> answerMap = new HashMap<>();
        answerMap.put("Stadt", null);
        return answerMap;
    }

}
