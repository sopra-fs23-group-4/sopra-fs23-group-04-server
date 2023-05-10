package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.LetterDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.RoundEndDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoundController {

    private final RoundService roundService;
    private final WebSocketService webSocketService;


    Logger log = LoggerFactory.getLogger(RoundController.class);

    RoundController(RoundService roundService,
                    WebSocketService webSocketService) {
        this.roundService = roundService;
        this.webSocketService=webSocketService;
    }

    /*@PutMapping("/games/{gamePin}/{roundNumber}/start")
    public void gameStart(@PathVariable("gamePin") int gamePin,
                          @PathVariable("roundNumber") int roundNumber) {

        LetterDTO letterDTO = roundService.startRound(gamePin, roundNumber);

        webSocketService.sendMessageToClients(Constant.defaultDestination+gamePin,letterDTO);

        log.info("Round " + roundNumber + "started in lobby " + gamePin);

        roundService.startRoundTime(gamePin);
    }*/

    @PutMapping("/games/{gamePin}/{roundNumber}/end")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void endRound(@PathVariable("gamePin") int gamePin,
                         @PathVariable("roundNumber") int roundNumber,
                         @RequestHeader("Authorization") String userToken) {
        log.info(" "+gamePin + roundNumber+ userToken);

        roundService.endRound(gamePin, userToken, roundNumber);

        String fill="roundEnd";
        RoundEndDTO roundEndDTO=new RoundEndDTO();
        roundEndDTO.setRounded(fill);
        webSocketService.sendMessageToClients(Constant.defaultDestination+gamePin, roundEndDTO);
        log.info("Round " + roundNumber + " ended in lobby " + gamePin);
}
}