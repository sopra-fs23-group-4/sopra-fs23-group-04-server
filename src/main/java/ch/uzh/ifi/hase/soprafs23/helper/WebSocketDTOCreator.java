package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.websocketDto.WebSocketDTO;

public class WebSocketDTOCreator {

    public static WebSocketDTO resultWinner(){
        WebSocketDTO webSocketDTO=new WebSocketDTO();
        webSocketDTO.setType("resultWinner");
        return webSocketDTO;
    }

    public static WebSocketDTO resultScoreBoard(){
        WebSocketDTO webSocketDTO=new WebSocketDTO();
        webSocketDTO.setType("resultScoreboard");
        return webSocketDTO;
    }
    public static WebSocketDTO resultNextVote(){
        WebSocketDTO webSocketDTO=new WebSocketDTO();
        webSocketDTO.setType("resultNextVote");
        return webSocketDTO;
    }
    public static WebSocketDTO votingEnd(){
        WebSocketDTO webSocketDTO=new WebSocketDTO();
        webSocketDTO.setType("votingEnd");
        return webSocketDTO;
    }



}
