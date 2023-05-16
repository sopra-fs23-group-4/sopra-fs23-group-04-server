package ch.uzh.ifi.hase.soprafs23.serviceIntegration;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

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
    private Game game;

    @Mock
    private User user;

    @Mock
    private Round round;

    @BeforeEach
    public void setUp() {
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
    public void createAllRounds_createsRoundsSuccessfully() {
        // Arrange
        Game game = new Game();
        game.setRoundLetters(Arrays.asList('a', 'b', 'c'));

        // Act
        roundService.createAllRounds(game);

        // Assert
        verify(roundRepository, times(3)).save(any(Round.class));
        verify(roundRepository, times(1)).flush();
    }


}


