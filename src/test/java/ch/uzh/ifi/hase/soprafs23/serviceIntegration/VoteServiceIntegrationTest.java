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
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class VoteServiceIntegrationTest {

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

        game = createGameForTesting(user1.getToken());

    }

    @Test
    void saveVoting_validInput() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();

        Map<String, String> answersUser1 = getCategoryAnswerMap();
        Map<String, String> answersUser2 = getCategoryEmptyAnswerMap();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        Round round = roundRepository.findByGameAndRoundNumber(game, 1);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user1Token, 1, answersUser1));
        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user2Token, 1, answersUser2));

        Map<Integer, String> votingOfUser1 = Map.of(1, "CORRECT_NOT_UNIQUE");
        Map<Integer, String> votingOfUser2 = Map.of(2, "CORRECT_UNIQUE");

        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user1Token, votingOfUser1));
        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user2Token, votingOfUser2));

    }

    @Test
    void saveVoting_invalidInput_CategoryAndAnswerDoNotMatch() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();

        Map<String, String> answers = getCategoryAnswerMap();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        Round round = roundRepository.findByGameAndRoundNumber(game, 1);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user1Token, 1, answers));
        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user2Token, 1, answers));

        Map<Integer, String> votingOfUser1 = Map.of(1, "CORRECT_NOT_UNIQUE");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> voteService.saveVote(gamePin, "Land", user1Token, votingOfUser1));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("The category of voting and the answers don't match.", exception.getReason());

    }

    @Test
    void saveVoting_invalidInput_InvalidVote() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();

        Map<String, String> answers = getCategoryAnswerMap();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        Round round = roundRepository.findByGameAndRoundNumber(game, 1);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user1Token, 1, answers));
        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user2Token, 1, answers));

        Map<Integer, String> votingOfUser1 = Map.of(1, "CORRECT_NOT_WRONG");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> voteService.saveVote(gamePin, categoryNames.get(0), user1Token, votingOfUser1));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("At least one of the votes is invalid!", exception.getReason());

    }

    @Test
    void getVoting_validInput() {

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
                2, "CORRECT_UNIQUE",
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

        AtomicReference<List<VoteGetDTO>> voteGetDTOList = new AtomicReference<>();
        voteGetDTOList.set(voteService.getVotes(gamePin, 1, categoryNames.get(0), user1Token));

        assertEquals(4, voteGetDTOList.get().size());

        assertEquals(user1.getUsername(), voteGetDTOList.get().get(0).getUsername());
        assertEquals(user2.getUsername(), voteGetDTOList.get().get(1).getUsername());
        assertEquals(user3.getUsername(), voteGetDTOList.get().get(2).getUsername());
        assertEquals(user4.getUsername(), voteGetDTOList.get().get(3).getUsername());

        assertEquals(3, voteGetDTOList.get().get(0).getPoints());
        assertEquals(3, voteGetDTOList.get().get(1).getPoints());
        assertEquals(1, voteGetDTOList.get().get(2).getPoints());
        assertEquals(0, voteGetDTOList.get().get(3).getPoints());

        assertEquals(1, voteGetDTOList.get().get(0).getNumberOfUnique());
        assertEquals(0, voteGetDTOList.get().get(0).getNumberOfNotUnique());
        assertEquals(1, voteGetDTOList.get().get(0).getNumberOfWrong());
        assertEquals(1, voteGetDTOList.get().get(0).getNumberOfNoVote());

        assertEquals(1, voteGetDTOList.get().get(1).getNumberOfUnique());
        assertEquals(1, voteGetDTOList.get().get(1).getNumberOfNotUnique());
        assertEquals(1, voteGetDTOList.get().get(1).getNumberOfWrong());
        assertEquals(0, voteGetDTOList.get().get(1).getNumberOfNoVote());

        assertEquals(0, voteGetDTOList.get().get(2).getNumberOfUnique());
        assertEquals(2, voteGetDTOList.get().get(2).getNumberOfNotUnique());
        assertEquals(0, voteGetDTOList.get().get(2).getNumberOfWrong());
        assertEquals(1, voteGetDTOList.get().get(2).getNumberOfNoVote());

        assertEquals(0, voteGetDTOList.get().get(3).getNumberOfUnique());
        assertEquals(0, voteGetDTOList.get().get(3).getNumberOfNotUnique());
        assertEquals(2, voteGetDTOList.get().get(3).getNumberOfWrong());
        assertEquals(1, voteGetDTOList.get().get(3).getNumberOfNoVote());
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
