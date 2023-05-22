package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoundController.class)
public class RoundControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoundService roundService;

    @Test
    public void givenRoundNumberAndGamePin_whenEndRound_thenReturnStatusOk() throws Exception {
        // Given
        int gamePin = 123;
        int roundNumber = 1;
        String userToken = "userToken";
        MockHttpServletRequestBuilder putRequest = put("/games/{gamePin}/{roundNumber}/end", gamePin, roundNumber)
                .header("Authorization", userToken);

        doNothing().when(roundService).stopRound(gamePin, userToken, roundNumber);

        // When


        // Then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }
    @Test
    public void givenRoundNumberAndGamePin_whenEndRound_thenReturnStatusNotFound() throws Exception {
        // Given
        int gamePin = 12344;
        int roundNumber = 1;
        String userToken = "userToken";
        MockHttpServletRequestBuilder putRequest = put("/games/{gamePin}/{roundNumber}/end", gamePin, roundNumber)
                .header("Authorization", userToken);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(roundService).stopRound(gamePin,userToken,roundNumber);


        // When


        // Then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenRoundNumberAndGamePin_whenEndRound_thenReturnStatusNotAuthorized() throws Exception {
        // Given
        int gamePin = 12344;
        int roundNumber = 1;
        String userToken = "userToken";
        MockHttpServletRequestBuilder putRequest = put("/games/{gamePin}/{roundNumber}/end", gamePin, roundNumber)
                .header("Authorization", userToken);

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(roundService).stopRound(gamePin,userToken,roundNumber);


        // When


        // Then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void givenGamePinAndUserToken_whenUserSkipRequest_thenReturnStatusNoContent() throws Exception {
        // Given
        int gamePin = 123;
        String userToken = "userToken";
        MockHttpServletRequestBuilder putRequest = put("/games/{gamePin}/skip", gamePin)
                .header("Authorization", userToken);

        doNothing().when(roundService).skipRequest(gamePin, userToken);

        // When
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        // Then
        // Additional verifications/assertions if needed
    }

    @Test
    public void givenGamePinAndUserToken_whenUserSkipRequest_thenReturnStatusNotFound() throws Exception {
        // Given
        int gamePin = 12344;
        String userToken = "userToken";
        MockHttpServletRequestBuilder putRequest = put("/games/{gamePin}/skip", gamePin)
                .header("Authorization", userToken);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(roundService).skipRequest(gamePin, userToken);

        // When
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());

        // Then
        // Additional verifications/assertions if needed
    }

    @Test
    public void givenGamePinAndUserToken_whenUserSkipRequest_thenReturnStatusUnauthorized() throws Exception {
        // Given
        int gamePin = 12344;
        String userToken = "userToken";
        MockHttpServletRequestBuilder putRequest = put("/games/{gamePin}/skip", gamePin)
                .header("Authorization", userToken);

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(roundService).skipRequest(gamePin, userToken);

        // When
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());

        // Then
        // Additional verifications/assertions if needed
    }




}
