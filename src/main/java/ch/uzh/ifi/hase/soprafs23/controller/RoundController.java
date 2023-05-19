package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocketDto.RoundEndDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoundController {

    private final RoundService roundService;
    private final WebSocketService webSocketService;


    Logger logger = LoggerFactory.getLogger(RoundController.class);

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


        roundService.stopRound(gamePin, userToken, roundNumber);

        String logInfo1 = String.format(
                "Round ended -> gamePin: %d, roundNumber: %d.",
                gamePin, roundNumber);
        logger.info(logInfo1);

    }

    @PutMapping("/games/{gamePin}/skip")
    @ResponseStatus(HttpStatus.OK)
    public void userSkipRequest(@RequestHeader("Authorization") String userToken,
                                @PathVariable("gamePin") int gamePin) {

        roundService.skipRequest(gamePin,userToken);
        logger.info("Skip Request from " + userToken);

    }
}