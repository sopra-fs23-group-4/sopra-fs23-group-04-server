package ch.uzh.ifi.hase.soprafs23.serviceIntegration;

import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private AnswerService answerService;
    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;

    private User user1;
    private User user2;
    private User user3;
    private Game game;

    @BeforeEach
    void setUp() {
        user1 = createUserForTesting();

        user2 = createUserForTesting();

        user3 = createUserForTesting();

        game = createGameForTesting(user1.getToken());

    }

    @Test
    void createAndReturnGame_validInput_gameCreated() {

        String user1Token = user1.getToken();

        AtomicReference<Game> createdGame = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            createdGame.set(gameService.createAndReturnGame(game, user1Token));
        });

        assertNotNull(createdGame.get());

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
        assertEquals("You are already part of a game." +
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
    void leaveGame_validInput_lastUser_gameDeleted() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> game = gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.leaveGame(gamePin, user1Token));

        assertNull(gameRepository.findByGamePin(gamePin));

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

        int userAmountTestGame = testGame.getUsers().size();

        assertEquals(user2Id, game.getHostId());
        assertEquals(1, userAmountTestGame);

    }

    @Test
    void setUpGameForStart_validInput_gameSetUp() {

        String user1Token = user1.getToken();

        assertDoesNotThrow(() -> gameService.createAndReturnGame(game, user1Token));
        int gamePin = game.getGamePin();

        assertDoesNotThrow(() -> gameService.setUpGameForStart(gamePin));

        Game testGame = gameRepository.findByGamePin(gamePin);

        assertEquals(GameStatus.RUNNING, game.getStatus());

    }

    @Test
    void getStandardCategories_validInput() {

        AtomicReference<GameCategoriesDTO> gameCategoriesDTO = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            gameCategoriesDTO.set(gameService.getStandardCategories());
        });

        assertEquals(GameCategory.getCategories(), gameCategoriesDTO.get().getCategories());

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

        return gameForCreation;
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
