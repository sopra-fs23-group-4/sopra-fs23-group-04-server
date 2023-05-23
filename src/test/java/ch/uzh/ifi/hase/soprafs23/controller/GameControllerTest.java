package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.helper.GameHelper;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.CategoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.service.CategoryService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
}
