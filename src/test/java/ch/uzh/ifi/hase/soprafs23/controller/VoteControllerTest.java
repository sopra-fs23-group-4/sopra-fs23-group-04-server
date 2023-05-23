package ch.uzh.ifi.hase.soprafs23.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs23.helper.VoteHelper;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.VoteService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class VoteControllerTest {

    @InjectMocks
    private VoteController voteController;

    @Mock
    private VoteHelper voteHelper;

    public VoteControllerTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveVoteTest() {
        // Arrange
        int gamePin = 1234;
        String categoryName = "category";
        String userToken = "token";
        Map<Integer, String> votings = new HashMap<>();
        votings.put(1, "vote"); // Add some test data

        VoteService voteServiceMock = Mockito.mock(VoteService.class);
        doNothing().when(voteServiceMock).saveVote(gamePin, categoryName, userToken, votings);

        VoteController voteController = new VoteController(voteServiceMock); // pass the mock to the controller

        // Act
        voteController.saveVote(gamePin, categoryName, userToken, votings);

        // Assert
        verify(voteServiceMock, times(1)).saveVote(gamePin, categoryName, userToken, votings);
    }

    @Test
    public void getVotesTest() {
        // Arrange
        int gamePin = 1234;
        int roundNumber = 1;
        String categoryName = "category";
        String userToken = "token";

        List<VoteGetDTO> expected = new ArrayList<>();
        expected.add(new VoteGetDTO()); // add expected DTOs

        VoteService voteServiceMock = Mockito.mock(VoteService.class);
        when(voteServiceMock.getVotes(gamePin, roundNumber, categoryName, userToken)).thenReturn(expected);

        VoteController voteController = new VoteController(voteServiceMock); // pass the mock to the controller

        // Act
        List<VoteGetDTO> result = voteController.getVotes(gamePin, roundNumber, categoryName, userToken);

        // Assert
        assertEquals(expected, result);
    }


}
