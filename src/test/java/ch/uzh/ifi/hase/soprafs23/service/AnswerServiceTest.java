package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.*;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Captor
    ArgumentCaptor<Answer> answerCaptor;

    private int gamePin;
    private String userToken;
    private int roundNumber;
    private Map<String, String> answers;

    @BeforeEach
    public void setUp() {
        gamePin = 12345;
        userToken = "abcde";
        roundNumber = 1;
        answers = new HashMap<>();
        answers.put("category1", "answer1");
        answers.put("category2", "answer2");
    }

    @Test
    public void testSaveAnswers_gameDoesNotExist() {
        when(gameRepository.findByGamePin(anyInt())).thenReturn(null);

        try {
            answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.NOT_FOUND);
            assert (Objects.equals(e.getReason(), "Game does not exist. Please try again with a different game!"));
        }
    }

    @Test
    public void testSaveAnswers_gameNotRunning() {
        Game notRunningGame = new Game();
        notRunningGame.setStatus(GameStatus.OPEN);
        when(gameRepository.findByGamePin(anyInt())).thenReturn(notRunningGame);

        try {
            answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.CONFLICT);
            assert (Objects.equals(e.getReason(), "Game is not running. Please try again with a different game!"));
        }
    }

    @Test
    public void testSaveAnswers_userDoesNotExist() {
        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(null);

        try {
            answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.NOT_FOUND);
            assert (Objects.equals(e.getReason(), "User does not exist. Please register before playing!"));
        }
    }

    @Test
    public void testSaveAnswers_userNotInGame() {
        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        User user = new User();
        user.setToken(userToken);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(user);

        try {
            answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.FORBIDDEN);
            assert (Objects.equals(e.getReason(), "User is not part of this game."));
        }
    }

    @Test
    public void testSaveAnswers_roundDoesNotExist() {
        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        User user = new User();
        user.setToken(userToken);

        runningGame.getUsers().add(user);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(user);
        lenient().when(roundRepository.findByGameAndRoundNumber(any(Game.class), anyInt())).thenReturn(null);

        try {
            answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
        } catch (ResponseStatusException e) {
            if (e.getStatus() == HttpStatus.FORBIDDEN) {
                assertEquals("User is not part of this game.", e.getReason());
            } else if (e.getStatus() == HttpStatus.NOT_FOUND) {
                assertEquals("Round does not exist.", e.getReason());
            } else {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    public void testSaveAnswers_roundHasNotFinished() {
        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        User user = new User();
        user.setToken(userToken);

        runningGame.getUsers().add(user);

        Round unfinishedRound = new Round();
        unfinishedRound.setStatus(RoundStatus.RUNNING);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(user);
        lenient().when(roundRepository.findByGameAndRoundNumber(any(Game.class), anyInt())).thenReturn(null);

        try {
            answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
        } catch (ResponseStatusException e) {
            if (e.getStatus() == HttpStatus.FORBIDDEN) {
                assertEquals("User is not part of this game.", e.getReason());
            } else if (e.getStatus() == HttpStatus.CONFLICT) {
                assertEquals("Round is not finished yet.", e.getReason());
            } else {
                fail("Unexpected exception");
            }
        }

    }

    @Test
    public void testGetAnswers_gameDoesNotExist() {
        int gamePin = 12345;
        int roundNumber = 1;
        String categoryName = "category1";
        String userToken = "abcde";

        when(gameRepository.findByGamePin(anyInt())).thenReturn(null);

        try {
            answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.NOT_FOUND);
            assert (Objects.equals(e.getReason(), "Game does not exist. Please try again with a different game!"));
        }
    }

    @Test
    public void testGetAnswers_gameNotRunning() {
        int gamePin = 12345;
        int roundNumber = 1;
        String categoryName = "category1";
        String userToken = "abcde";

        Game notRunningGame = new Game();
        notRunningGame.setStatus(GameStatus.OPEN);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(notRunningGame);

        try {
            answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.CONFLICT);
            assert (Objects.equals(e.getReason(), "Game is not running. Please try again with a different game!"));
        }
    }

    @Test
    public void testGetAnswers_userDoesNotExist() {
        int gamePin = 12345;
        int roundNumber = 1;
        String categoryName = "category1";
        String userToken = "abcde";

        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(null);

        try {
            answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.NOT_FOUND);
            assert (Objects.equals(e.getReason(), "User does not exist. Please register before playing!"));
        }
    }

    @Test
    public void testGetAnswers_userNotInGame() {
        int gamePin = 12345;
        int roundNumber = 1;
        String categoryName = "category1";
        String userToken = "abcde";

        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        User user = new User();
        user.setToken(userToken);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(user);

        try {
            answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
        }
        catch (ResponseStatusException e) {
            assert (e.getStatus() == HttpStatus.FORBIDDEN);
            assert (Objects.equals(e.getReason(), "User is not part of this game."));
        }
    }

    @Test
    public void testGetAnswers_roundDoesNotExist() {
        int gamePin = 12345;
        int roundNumber = 1;
        String categoryName = "category1";
        String userToken = "abcde";

        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        User user = new User();
        user.setToken(userToken);
        runningGame.getUsers().add(user);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(user);
        lenient().when(roundRepository.findByGameAndRoundNumber(any(Game.class), anyInt())).thenReturn(null);

        try {
            answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
        }
        catch (ResponseStatusException e) {
            if (e.getStatus() == HttpStatus.FORBIDDEN) {
                assertEquals("User is not part of this game.", e.getReason());
            } else if (e.getStatus() == HttpStatus.NOT_FOUND) {
                assertEquals("Round does not exist.", e.getReason());
            } else {
                fail("Unexpected exception");
            }
        }

    }

    @Test
    public void testGetAnswers_roundNotFinished() {
        int gamePin = 12345;
        int roundNumber = 1;
        String categoryName = "category1";
        String userToken = "abcde";

        Game runningGame = new Game();
        runningGame.setStatus(GameStatus.RUNNING);
        User user = new User();
        user.setToken(userToken);
        runningGame.getUsers().add(user);

        Round unfinishedRound = new Round();
        unfinishedRound.setStatus(RoundStatus.RUNNING);

        when(gameRepository.findByGamePin(anyInt())).thenReturn(runningGame);
        when(userRepository.findByToken(anyString())).thenReturn(user);
        lenient().when(roundRepository.findByGameAndRoundNumber(any(Game.class), anyInt())).thenReturn(unfinishedRound);

        try {
            answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
        }
        catch (ResponseStatusException e) {
            if (e.getStatus() == HttpStatus.FORBIDDEN) {
                assertEquals("User is not part of this game.", e.getReason());
            } else if (e.getStatus() == HttpStatus.CONFLICT) {
                assertEquals("Round is not finished yet.", e.getReason());
            } else {
                fail("Unexpected exception");
            }
        }
    }
}