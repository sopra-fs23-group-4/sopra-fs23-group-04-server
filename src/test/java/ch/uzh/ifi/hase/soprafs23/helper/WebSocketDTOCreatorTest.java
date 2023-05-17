package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.websocketDto.WebSocketDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WebSocketDTOCreatorTest {

    @Test
    public void testResultWinnerType() {
        WebSocketDTO webSocketDTO = WebSocketDTOCreator.resultWinner();
        Assertions.assertEquals("resultWinner", webSocketDTO.getType());
    }

    @Test
    public void testResultScoreBoardType() {
        WebSocketDTO webSocketDTO = WebSocketDTOCreator.resultScoreBoard();
        Assertions.assertEquals("resultScoreboard", webSocketDTO.getType());
    }

    @Test
    public void testResultNextVoteType() {
        WebSocketDTO webSocketDTO = WebSocketDTOCreator.resultNextVote();
        Assertions.assertEquals("resultNextVote", webSocketDTO.getType());
    }

    @Test
    public void testVotingEndType() {
        WebSocketDTO webSocketDTO = WebSocketDTOCreator.votingEnd();
        Assertions.assertEquals("votingEnd", webSocketDTO.getType());
    }
}