package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.WebsocketService;
import ch.uzh.ifi.hase.soprafs23.webSockets.DTO.LetterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class WebsocketController {
    private final WebsocketService websocketService;
    private final GameService gameService;
    private final static String destination = "/topic/lobbies/";
    Logger log = LoggerFactory.getLogger(WebsocketController.class);

    public WebsocketController(WebsocketService websocketService, GameService gameService){
        this.websocketService=websocketService;
        this.gameService=gameService;
    }

    @MessageMapping("/lobbies/{lobbyId}/start-game")
    public void startGame(@DestinationVariable Long  lobbyId) throws IOException{
        log.info("Lobby {}: Game started.", lobbyId);
        gameService.startGame(lobbyId);
        LetterDTO letterToSend =gameService.startNextRound(lobbyId);
        this.websocketService.sendMessageToClients(destination + lobbyId, letterToSend);
    }

    @MessageMapping("/lobbies/{lobbyId}/next-round")
    public void startNextRound(@DestinationVariable Long lobbyId) {
        log.info("Next round started for {lobby}");
        LetterDTO letterToSend =gameService.startNextRound(lobbyId);
        this.websocketService.sendMessageToClients(destination + lobbyId, letterToSend);
    }

}
