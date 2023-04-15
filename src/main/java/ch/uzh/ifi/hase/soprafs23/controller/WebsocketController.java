package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.WebsocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class WebsocketController {
    private final WebsocketService websocketService;
    Logger log = LoggerFactory.getLogger(WebsocketController.class);

    public WebsocketController(WebsocketService websocketService){
        this.websocketService=websocketService;
    }

    @MessageMapping("/lobbies/{lobbyId}/start-game")
    public void startGame(@DestinationVariable int lobbyId) throws IOException{
        log.info("Lobby {}: Game started.", lobbyId);
        //gameService.startGame(lobbyId);
        //LetterDTO questionToSend = gameService.startNextRound(lobbyId);
        //this.webSocketService.sendMessageToClients(destination + lobbyId, questionToSend);
    }




}
