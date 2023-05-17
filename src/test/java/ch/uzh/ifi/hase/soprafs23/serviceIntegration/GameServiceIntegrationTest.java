package ch.uzh.ifi.hase.soprafs23.serviceIntegration;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.helper.GameHelper;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.LeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.ScoreboardGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.WinnerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.service.*;
import ch.uzh.ifi.hase.soprafs23.websocketDto.GameUsersDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GameServiceIntegrationTest {
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
    void createAndReturnGame_validInput_gameCreated() {

        String user1Token = user1.getToken();

        AtomicReference<Game> createdGame = new AtomicReference<>();

        assertDoesNotThrow(() -> createdGame.set(gameService.createAndReturnGame(game, user1Token)));

        assertNotNull(createdGame.get());

    }

    @Test
    void createAndReturnGame_invalidInput_gameCreatedTwice() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> gameService.createAndReturnGame(game, user1Token));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.createAndReturnGame(game, user1Token));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("You are already part of a game. " +
                "You cannot host another game!", exception.getReason());

    }

    @Test
    void joinGame_validInput_gameJoined() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));

        WebSocketService mockWebSocketService = Mockito.mock(WebSocketService.class);
        doNothing().when(mockWebSocketService).sendMessageToClients(anyString(), any());

    }

    @Test
    void joinGame_invalidInput_gameJoinedTwice() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        WebSocketService mockWebSocketService = Mockito.mock(WebSocketService.class);
        doNothing().when(mockWebSocketService).sendMessageToClients(anyString(), any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.joinGame(gamePin, user1Token));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("You are already part of a game. " +
                "You cannot join another game!", exception.getReason());

    }

    @Test
    void joinGame_invalidInput_gameDoesNotExist() {

        String user1Token = user1.getToken();

        int gamePin = game.getGamePin();

        WebSocketService mockWebSocketService = Mockito.mock(WebSocketService.class);
        doNothing().when(mockWebSocketService).sendMessageToClients(anyString(), any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.joinGame(gamePin, user1Token));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game does not exist. Please try again with a different game!", exception.getReason());

    }

    @Test
    void leaveGame_validInput_lastUser_gameClosed() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.leaveGame(gamePin, user1Token));

        Game closedGame = gameRepository.findByGamePin(gamePin);
        assertNotNull(closedGame); // The game should still exist
        assertEquals(GameStatus.CLOSED, closedGame.getStatus()); // The game should now be CLOSED
    }

    @Test
    void leaveGame_validInput_gameHasOtherUser_userIsHost_newHost() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();
        int user2Id = user2.getId();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));
        assertDoesNotThrow(() -> gameService.leaveGame(gamePin, user1Token));

        Game testGame = gameRepository.findByGamePin(gamePin);

        int userAmountTestGame = testGame.getActiveUsers().size();

        assertEquals(user2Id, game.getHostId());
        assertEquals(1, userAmountTestGame);

    }

    @Test
    void setUpGameForStart_validInput_gameSetUp() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.setUpGameForStart(gamePin));

        Game testGame = gameRepository.findByGamePin(gamePin);

        assertEquals(GameStatus.RUNNING, game.getStatus());

    }

    @Test
    void getGameCategoriesByGamePin_validInput() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        AtomicReference<GameCategoriesDTO> gameCategoriesDTOAtomicReference = new AtomicReference<>();

        assertDoesNotThrow(() -> gameCategoriesDTOAtomicReference.set(gameService.getGameCategoriesByGamePin(gamePin)));

        assertEquals(categoryNames, gameCategoriesDTOAtomicReference.get().getCategories());

    }

    @Test
    void getGameUsersByGamePin_validInput() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));

        AtomicReference<GameUsersDTO> gameUsersDTOAtomicReference = new AtomicReference<>();

        assertDoesNotThrow(() -> gameUsersDTOAtomicReference.set(gameService.getGameUsersByGamePin(gamePin)));

        List<String> gameUsernamesWithoutHost = new ArrayList<>();
        gameUsernamesWithoutHost.add(user2.getUsername());

        assertEquals(gameUsernamesWithoutHost, gameUsersDTOAtomicReference.get().getUsernames());
        assertEquals(user1.getUsername(), gameUsersDTOAtomicReference.get().getHostUsername());

    }

    @Test
    void getGameCategoryNames_validInput() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        AtomicReference<List<String>> gameCategoryNames = new AtomicReference<>();

        assertDoesNotThrow(() -> gameCategoryNames.set(GameHelper.getCategoryNamesByGame(game)));

        assertEquals(categoryNames, gameCategoryNames.get());

    }

    @Test
    void getWinner_validInput() {

        String user1Token = user1.getToken();
        String user2Token = user2.getToken();
        String user3Token = user3.getToken();
        String user4Token = user4.getToken();

        Map<String, String> answers = getCategoryAnswerMap();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user2Token));
        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user3Token));
        assertDoesNotThrow(() -> gameService.joinGame(gamePin, user4Token));

        game.setStatus(GameStatus.RUNNING);
        gameRepository.saveAndFlush(game);

        Round round = roundRepository.findByGameAndRoundNumber(game, 1);

        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);

        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user1Token, 1, answers));
        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user2Token, 1, answers));
        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user3Token, 1, answers));
        assertDoesNotThrow(() -> answerService.saveAnswers(gamePin, user4Token, 1, answers));

        Map<Integer, String> votingForUser1 = Map.of(1, "CORRECT_NOT_UNIQUE");
        Map<Integer, String> votingForUser2 = Map.of(2, "CORRECT_UNIQUE");
        Map<Integer, String> votingForUser3 = Map.of(3, "CORRECT_UNIQUE");
        Map<Integer, String> votingForUser4 = Map.of(4, "WRONG");

        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user1Token, votingForUser1));
        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user2Token, votingForUser2));
        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user3Token, votingForUser3));
        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user4Token, votingForUser4));

        AtomicReference<List<WinnerGetDTO>> winnerGetDTOList = new AtomicReference<>();

        assertDoesNotThrow(() -> winnerGetDTOList.set(gameService.getWinner(gamePin)));

        List<String> actualWinnerUsernames = new ArrayList<>();
        for (int i = 0; i < winnerGetDTOList.get().size(); i++) {
            actualWinnerUsernames.add(winnerGetDTOList.get().get(i).getUsername());
        }

        List<String> expectedWinnerUsernames = new ArrayList<>();
        expectedWinnerUsernames.add(user2.getUsername());
        expectedWinnerUsernames.add(user3.getUsername());

        assertEquals(2, winnerGetDTOList.get().size());
        assertEquals(expectedWinnerUsernames, actualWinnerUsernames);

    }

    @Test
    void getScoreboard_validInput() {

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

        Map<Integer, String> votingForUser1 = Map.of(1, "CORRECT_NOT_UNIQUE");
        Map<Integer, String> votingForUser2 = Map.of(2, "CORRECT_UNIQUE");

        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user1Token, votingForUser1));
        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user2Token, votingForUser2));

        AtomicReference<List<ScoreboardGetDTO>> scoreboardGetDTOList = new AtomicReference<>();

        assertDoesNotThrow(() -> scoreboardGetDTOList.set(gameService.getScoreboard(gamePin)));

        List<String> actualScoreboard = new ArrayList<>();
        for (int i = 0; i < scoreboardGetDTOList.get().size(); i++) {
            actualScoreboard.add(scoreboardGetDTOList.get().get(i).getUsername());
        }

        List<String> expectedScoreboard = new ArrayList<>();
        expectedScoreboard.add(user2.getUsername());
        expectedScoreboard.add(user1.getUsername());

        assertEquals(2, scoreboardGetDTOList.get().size());
        assertEquals(expectedScoreboard, actualScoreboard);

    }

    @Test
    void getLeaderboard_validInput() {

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

        Map<Integer, String> votingForUser1 = Map.of(1, "CORRECT_NOT_UNIQUE");
        Map<Integer, String> votingForUser2 = Map.of(2, "CORRECT_UNIQUE");

        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user1Token, votingForUser1));
        assertDoesNotThrow(() -> voteService.saveVote(gamePin, categoryNames.get(0), user2Token, votingForUser2));

        AtomicReference<List<LeaderboardGetDTO>> leaderboardGetDTOList = new AtomicReference<>();

        assertDoesNotThrow(() -> leaderboardGetDTOList.set(gameService.getLeaderboard()));

        List<String> actualLeaderboard = new ArrayList<>();
        for (int i = 0; i < leaderboardGetDTOList.get().size(); i++) {
            actualLeaderboard.add(leaderboardGetDTOList.get().get(i).getUsername());
        }

        List<String> expectedLeaderboard = new ArrayList<>();
        expectedLeaderboard.add(user2.getUsername());
        expectedLeaderboard.add(user1.getUsername());
        expectedLeaderboard.add(user3.getUsername());
        expectedLeaderboard.add(user4.getUsername());

        assertEquals(4, leaderboardGetDTOList.get().size());
        assertEquals(expectedLeaderboard, actualLeaderboard);

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

}
