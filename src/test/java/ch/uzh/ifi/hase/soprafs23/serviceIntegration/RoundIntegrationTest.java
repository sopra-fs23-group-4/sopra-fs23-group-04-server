package ch.uzh.ifi.hase.soprafs23.serviceIntegration;
import static ch.uzh.ifi.hase.soprafs23.constant.GameCategory.getCategories;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.entity.game.SkipManager;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.SkipRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocketDto.LetterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RoundIntegrationTest {

    @InjectMocks
    private RoundService roundService;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private QuoteService quoteService;



    @Mock
    private User user;

    @Mock
    private Round round;
    private final List<String> categoryNames = List.of("Stadt");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    public void stopRound_roundIsRunning_statusSetToFinished() {
        // Arrange
        int gamePin = 1234;
        String userToken = "token";
        int roundNumber = 1;
        game.setStatus(GameStatus.RUNNING);

        when(gameRepository.findByGamePin(gamePin)).thenReturn(game);
        when(userRepository.findByToken(userToken)).thenReturn(user);
        when(roundRepository.findByGameAndRoundNumber(game, roundNumber)).thenReturn(round);

        // Act
        roundService.stopRound(gamePin, userToken, roundNumber);

        // Assert
        verify(round).setStatus(RoundStatus.FINISHED);
        verify(roundRepository).saveAndFlush(round);
    }*/

    @Test
    void createAllRounds_createsRoundsSuccessfully() {
        // Arrange
        Game game = new Game();
        game.setRoundLetters(Arrays.asList('a', 'b', 'c'));

        // Act
        roundService.createAllRounds(game);

        // Assert
        verify(roundRepository, times(3)).save(any(Round.class));
        verify(roundRepository, times(1)).flush();
    }
    @Test
    void stopRoundTest() {
        // Arrange
        Game game = new Game();
        User user = new User();
        Round round = new Round();
        game.setStatus(GameStatus.RUNNING);
        user.setToken("usertoken");
        game.addPlayer(user);
        round.setStatus(RoundStatus.RUNNING);


        when(gameRepository.findByGamePin(5785)).thenReturn(game);
        when(userRepository.findByToken("usertoken")).thenReturn(user);
        when(roundRepository.findByGameAndRoundNumber(game, 5)).thenReturn(round);

        // Act
        roundService.stopRound(5785,"usertoken" , 5);

        // Assert
        verify(roundRepository, times(1)).saveAndFlush(round);
        verify(userRepository, times(1)).findByToken("usertoken");
        verify(roundRepository, times(1)).findByGameAndRoundNumber(game, 5);

        assertEquals(RoundStatus.FINISHED, round.getStatus());
    }

    @Test
    void nextRoundTest() {
        int nextRound=5;
        // Arrange
        Game game = new Game();
        game.setCurrentRound(4);
        game.setGamePin(4500);

        Round round = new Round();
        round.setRoundNumber(5);
        round.setLetter('a');


        when(gameRepository.findByGamePin(game.getGamePin())).thenReturn(game);
        when(roundRepository.findByGameAndRoundNumber(game, 5)).thenReturn(round);
        ArgumentCaptor<LetterDTO> letterDtoCaptor = ArgumentCaptor.forClass(LetterDTO.class);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        roundService.nextRound(game.getGamePin());

        // Assert
        verify(roundRepository, times(1)).saveAndFlush(round);
        verify(gameRepository, times(1)).saveAndFlush(game);
        verify(gameRepository, times(1)).findByGamePin(game.getGamePin());
        verify(roundRepository, times(1)).findByGameAndRoundNumber(game, 5);
        verify(webSocketService, times(1)).sendMessageToClients(stringCaptor.capture(), letterDtoCaptor.capture());
        verify(webSocketService, times(1)).sendMessageToClients(stringCaptor.capture(), letterDtoCaptor.capture());

        assertEquals(RoundStatus.RUNNING, round.getStatus());
        assertEquals(game.getCurrentRound(),5);
        String expectedDestination = Constant.DEFAULT_DESTINATION + game.getGamePin();
        assertEquals(expectedDestination, stringCaptor.getValue());
        // Add additional assertions for game incrementRound method and LetterDTO object
    }
    @Test
    void testSkipRequest_pass() {
        // Arrange


        String userToken = "abcde12345";

        User user = new User();
        user.setToken(userToken);
        Game game = createGameForTesting();
        game.addPlayer(user);
        SkipManager skipManager = SkipRepository.addGame(game.getGamePin());
        skipManager.addUser(user);
        gameRepository.save(game);

        // Mocking repository methods
        when(userRepository.findByToken(userToken)).thenReturn(user);
        when(gameRepository.findByGamePin(game.getGamePin())).thenReturn(game);

        // Act
        roundService.skipRequest(game.getGamePin(), userToken);

        // Assert
        verify(userRepository, times(1)).findByToken(userToken);
        verify(gameRepository, times(1)).findByGamePin(game.getGamePin());

        assertTrue(skipManager.allPlayersWantToContinue());
    }

    @Test
    void testSkipRequest_fail() {
        // Arrange


        String userToken = "abcde12345";

        User user = new User();
        user.setToken(userToken);
        Game game = createGameForTesting();
        game.addPlayer(user);
        SkipManager skipManager = SkipRepository.addGame(game.getGamePin());
        skipManager.addUser(user);
        gameRepository.save(game);

        // Mocking repository methods
        when(userRepository.findByToken(userToken)).thenReturn(user);
        when(gameRepository.findByGamePin(game.getGamePin())).thenReturn(game);
        roundService.skipRequest(game.getGamePin(),userToken);

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> roundService.skipRequest(game.getGamePin(), userToken));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        // Assert
    }

    @Test

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





}


