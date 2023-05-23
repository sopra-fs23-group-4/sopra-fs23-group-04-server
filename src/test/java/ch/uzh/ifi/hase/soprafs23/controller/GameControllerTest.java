package ch.uzh.ifi.hase.soprafs23.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs23.rest.dto.game.LeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.ScoreboardGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.WinnerGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class GameControllerTest {

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    public GameControllerTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getGameUsersByGamePinTest() {
        // Arrange
        int gamePin = 1234;

        // Act
        gameController.getGameUsersByGamePin(gamePin);

        // Assert
        verify(gameService, times(1)).getGameUsersByGamePin(gamePin);
    }

    @Test
    public void joinGameTest() {
        // Arrange
        int gamePin = 1234;
        String userToken = "sampleUserToken";

        // Act
        gameController.joinGame(gamePin, userToken);

        // Assert
        verify(gameService, times(1)).joinGame(gamePin, userToken);
    }

    @Test
    public void leaveGameTest() {
        // Arrange
        int gamePin = 1234;
        String userToken = "sampleUserToken";

        // Act
        gameController.leaveGame(gamePin, userToken);

        // Assert
        verify(gameService, times(1)).leaveGame(gamePin, userToken);
    }
    @Test
    public void getWinnerTest() {
        // Arrange
        int gamePin = 1234;

        WinnerGetDTO winner1 = new WinnerGetDTO();
        winner1.setUsername("Winner1");
        winner1.setScore(200);
        winner1.setQuote("Winning isn't everything, it's the only thing.");

        List<WinnerGetDTO> expectedWinners = List.of(winner1);

        when(gameService.getWinner(gamePin)).thenReturn(expectedWinners);

        // Act
        List<WinnerGetDTO> actualWinners = gameController.getWinner(gamePin);

        // Assert
        assertEquals(expectedWinners, actualWinners);
    }
    @Test
    public void getScoreboardTest() {
        // Arrange
        int gamePin = 1234;

        ScoreboardGetDTO user1 = new ScoreboardGetDTO();
        user1.setUsername("User1");
        user1.setScore(100);

        ScoreboardGetDTO user2 = new ScoreboardGetDTO();
        user2.setUsername("User2");
        user2.setScore(200);

        List<ScoreboardGetDTO> expectedScoreboard = Arrays.asList(user1, user2);

        when(gameService.getScoreboard(gamePin)).thenReturn(expectedScoreboard);

        // Act
        List<ScoreboardGetDTO> actualScoreboard = gameController.getScoreboard(gamePin);

        // Assert
        assertEquals(expectedScoreboard, actualScoreboard);
    }
    @Test
    public void getLeaderboardTest() {
        // Arrange
        LeaderboardGetDTO user1 = new LeaderboardGetDTO();
        user1.setUsername("User1");
        user1.setAccumulatedScore(100);

        LeaderboardGetDTO user2 = new LeaderboardGetDTO();
        user2.setUsername("User2");
        user2.setAccumulatedScore(200);

        List<LeaderboardGetDTO> expectedLeaderboard = Arrays.asList(user1, user2);

        when(gameService.getLeaderboard()).thenReturn(expectedLeaderboard);

        // Act
        List<LeaderboardGetDTO> actualLeaderboard = gameController.getLeaderboard();

        // Assert
        assertEquals(expectedLeaderboard, actualLeaderboard);
    }
}

