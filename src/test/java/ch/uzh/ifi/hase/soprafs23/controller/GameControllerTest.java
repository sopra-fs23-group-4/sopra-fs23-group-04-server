package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.helper.GameHelper;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.*;
import ch.uzh.ifi.hase.soprafs23.service.CategoryService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.service.VoteService;
import ch.uzh.ifi.hase.soprafs23.websocketDto.GameUsersDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private RoundService roundService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private VoteService voteService;

    /*@Test
    void givenGamePostDTOAndUserToken_whenCreateGame_thenReturnGamePin() throws Exception {
        // given
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setHostId(123);
        gamePostDTO.setRounds(5);
        gamePostDTO.setRoundLength(RoundLength.MEDIUM);
        gamePostDTO.setCategories(Arrays.asList("category1", "category2"));
        // Set up the gamePostDTO object with the required properties for testing

        String userToken = "your-user-token";

        Game game = new Game();
        game.setGamePin(1234);

        given(gameService.createAndReturnGame(any(Game.class), eq(userToken))).willReturn(game);

        // when
        mockMvc.perform(post("/games/lobbies/creation")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(gamePostDTO)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.gamePin").value(game.getGamePin()));
    }*/
    @Test
    void givenGamePin_whenGameStart_thenSetupGameAndStartNextRound() throws Exception {
        // given
        int gamePin = 1234;

        // when
        MockHttpServletRequestBuilder putRequest= MockMvcRequestBuilders.put("/games/{gamePin}/start", gamePin)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        // then
        verify(gameService).setUpGameForStart(gamePin);
        verify(roundService).nextRound(gamePin);
        verify(roundService).startRoundTime(gamePin);
    }



    private static String toJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }


    @Test
    void givenStandardCategories_whenGetStandardCategories_thenReturnCategoriesDTO() throws Exception {
        // given
        GameCategoriesDTO categoriesDTO = new GameCategoriesDTO();
        // Set up the categoriesDTO object with the required properties for testing

        MockHttpServletRequestBuilder getRequest =get("/games/categories")
                .contentType(MediaType.APPLICATION_JSON);

        // when
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        // the
    }

    @Test
    void givenRandomCategory_whenGetRandomCategory_thenReturnCategoryDTO() throws Exception {
        // given
        CategoryGetDTO categoryDTO = new CategoryGetDTO();
        // Set up the categoryDTO object with the required properties for testing


        // when
        mockMvc.perform(get("/games/randomCategories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        // then
    }
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

    @Test
    void whenGetGameUsersByGamePin_givenGamePin_thenInvokeServiceMethodAndReturnDto() throws Exception {
        // given
        int gamePin = 1234;
        GameUsersDTO expectedDto = new GameUsersDTO();
        // populate expectedDto with some sample data...

        when(gameService.getGameUsersByGamePin(gamePin)).thenReturn(expectedDto);

        // when
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/games/{gamePin}/users", gamePin)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        // then
        verify(gameService).getGameUsersByGamePin(gamePin);
    }

    @Test
    void whenLeaveGame_givenGamePinAndUserToken_thenInvokeServiceMethod() throws Exception {
        // given
        int gamePin = 1234;
        String userToken = "testUserToken";

        // no return expected, service method is void

        // when
        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/games/lobbies/{gamePin}/leave", gamePin)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", userToken);

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent()); // expect HTTP 204 (NO CONTENT)

        // then
        verify(gameService).leaveGame(gamePin, userToken);
    }
    @Test
    void whenJoinGame_givenGamePinAndUserToken_thenInvokeServiceMethod() throws Exception {
        // given
        int gamePin = 1234;
        String userToken = "testUserToken";

        // no return expected, service method is void

        // when
        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/games/lobbies/{gamePin}/join", gamePin)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", userToken);

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent()); // expect HTTP 204 (NO CONTENT)

        // then
        verify(gameService).joinGame(gamePin, userToken);
    }


    @Test
    void whenGetWinner_givenGamePin_thenInvokeServiceMethod() throws Exception {
        // given
        int gamePin = 1234;
        List<WinnerGetDTO> winners = new ArrayList<>();
        WinnerGetDTO winnerGetDTO = new WinnerGetDTO(); // Assume WinnerGetDTO has a default constructor.
        winners.add(winnerGetDTO);
        when(gameService.getWinner(gamePin)).thenReturn(winners);

        // when
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/games/lobbies/{gamePin}/winner", gamePin)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(winners)));

        // then
        verify(gameService).getWinner(gamePin);
    }

    @Test
    void whenGetScoreboard_givenGamePin_thenInvokeServiceMethod() throws Exception {
        // given
        int gamePin = 1234;
        List<ScoreboardGetDTO> scoreboard = new ArrayList<>();
        ScoreboardGetDTO scoreboardGetDTO = new ScoreboardGetDTO(); // Assume ScoreboardGetDTO has a default constructor.
        scoreboard.add(scoreboardGetDTO);
        when(gameService.getScoreboard(gamePin)).thenReturn(scoreboard);

        // when
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/games/lobbies/{gamePin}/scoreboard", gamePin)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(scoreboard)));

        // then
        verify(gameService).getScoreboard(gamePin);
    }


    @Test
    void whenGetLeaderboard_thenInvokeServiceMethod() throws Exception {
        // given
        List<LeaderboardGetDTO> leaderboard = new ArrayList<>();
        LeaderboardGetDTO leaderboardGetDTO = new LeaderboardGetDTO(); // Assume LeaderboardGetDTO has a default constructor.
        leaderboard.add(leaderboardGetDTO);
        when(gameService.getLeaderboard()).thenReturn(leaderboard);

        // when
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/games/lobbies/leaderboard")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(leaderboard)));

        // then
        verify(gameService).getLeaderboard();
    }

}
