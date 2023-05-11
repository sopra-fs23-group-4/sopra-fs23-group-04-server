package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {
    //@Test
    //void getScoreboard_validGameId_returnsScoreboard() {
    //    // Setup test data
    //    int gameId = 1;
//
    //    GameRepository gameRepository = mock(GameRepository.class);
    //    UserRepository userRepository = mock(UserRepository.class);
    //    RoundRepository roundRepository = mock(RoundRepository.class);
    //    RoundService roundService = mock(RoundService.class);
    //    AnswerRepository answerRepository = mock(AnswerRepository.class);
//
    //    GameService gameService = spy(new GameService(gameRepository, userRepository, roundRepository, roundService, answerRepository));
//
    //    User user1 = new User();
    //    User user2 = new User();
    //    int user1Score = 10;
    //    int user2Score = 20;
    //    Map<User, Integer> userScores = new HashMap<>();
    //    userScores.put(user1, user1Score);
    //    userScores.put(user2, user2Score);
//
    //    // Mock the private method indirectly using spy
    //    doReturn(userScores).when(gameService).calculateUserScores(gameId);
//
    //    // Call the method
    //    List<ScoreboardGetDTO> scoreboard = gameService.getScoreboard(gameId);
//
    //    // Verify results
    //    assertEquals(2, scoreboard.size());
//
    //    ScoreboardGetDTO entry1 = scoreboard.get(0);
    //    assertEquals(user1, entry1.getUser());
    //    assertEquals(user1Score, entry1.getScore());
//
    //    ScoreboardGetDTO entry2 = scoreboard.get(1);
    //    assertEquals(user2, entry2.getUser());
    //    assertEquals(user2Score, entry2.getScore());
    //}
    //@Test
    //void constructor_initializesCorrectly() {
    //    GameRepository gameRepository = mock(GameRepository.class);
    //    RoundRepository roundRepository = mock(RoundRepository.class);
    //    AnswerRepository answerRepository = mock(AnswerRepository.class);
    //    UserRepository userRepository = mock(UserRepository.class);
//
    //    RoundService roundService = mock(RoundService.class);
    //    WebSocketService webSocketService = mock(WebSocketService.class);
//
    //    GameService gameService = new GameService(
    //            gameRepository, roundRepository, answerRepository, userRepository,
    //            voteRepository, roundService, webSocketService);
//
    //    assertNotNull(gameService);
    //}

    //@Test
    //void joinGame_validGamePinAndUserToken_returnsGameUsersDTO() {
    //    int gamePin = 1234;
    //    String userToken = "test-token";
    //    User user = new User();
    //    Game gameToJoin = new Game();
    //    gameToJoin.setGamePin(gamePin);
//
    //    GameRepository gameRepository = mock(GameRepository.class);
    //    UserRepository userRepository = mock(UserRepository.class);
    //    RoundRepository roundRepository = mock(RoundRepository.class);
    //    RoundService roundService = mock(RoundService.class);
    //    AnswerRepository answerRepository = mock(AnswerRepository.class);
//
    //    GameService gameService = spy(new GameService(gameRepository, userRepository, roundRepository, roundService, answerRepository));
//
    //    when(userRepository.findByToken(userToken)).thenReturn(user);
    //    when(gameRepository.findByGamePin(gamePin)).thenReturn(gameToJoin);
//
    //    // Mock the private methods indirectly using spy
    //    doNothing().when(gameService).checkIfUserExists(user);
    //    doNothing().when(gameService).checkIfUserCanJoin(user.getId());
//
    //    GameUsersDTO expectedGameUsersDTO = new GameUsersDTO();
    //    doReturn(expectedGameUsersDTO).when(gameService).getHostAndAllUserNamesOfGame(gameToJoin);
//
    //    // Call the method
    //    GameUsersDTO resultGameUsersDTO = gameService.joinGame(gamePin, userToken);
//
    //    // Verify results
    //    assertEquals(expectedGameUsersDTO, resultGameUsersDTO);
    //}
}
