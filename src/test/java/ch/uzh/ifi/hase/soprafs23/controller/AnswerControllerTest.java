package ch.uzh.ifi.hase.soprafs23.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ch.uzh.ifi.hase.soprafs23.service.AnswerService;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(AnswerController.class)
public class AnswerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnswerService answerService;

    @Test
    public void givenGamePinAndRoundNumber_whenSaveAnswers_thenCallsAnswerServiceAndLogsInfo() throws Exception {
        // given
        int gamePin = 1234;
        int roundNumber = 1;
        String userToken = "user-token";
        Map<String, String> answers = new HashMap<>();
        answers.put("question1", "answer1");
        answers.put("question2", "answer2");

        // when
        MockHttpServletRequestBuilder postRequest = MockMvcRequestBuilders
                .post("/games/{gamePin}/{roundNumber}", gamePin, roundNumber)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(answers));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

        // then
        ArgumentCaptor<Integer> gamePinCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> userTokenCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> roundNumberCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Map<String, String>> answersCaptor = ArgumentCaptor.forClass(Map.class);
        verify(answerService).saveAnswers(gamePinCaptor.capture(), userTokenCaptor.capture(),
                roundNumberCaptor.capture(), answersCaptor.capture());

        assertEquals(gamePin, gamePinCaptor.getValue());
        assertEquals(userToken, userTokenCaptor.getValue());
        assertEquals(roundNumber, roundNumberCaptor.getValue());
        assertEquals(answers, answersCaptor.getValue());
    }


}
