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


    Logger logger = LoggerFactory.getLogger(RoundController.class);

    RoundController(RoundService roundService) {
        this.roundService = roundService;
    }


    @PutMapping("/games/{gamePin}/{roundNumber}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userSkipRequest(@RequestHeader("Authorization") String userToken,
                                @PathVariable("gamePin") int gamePin) {

        roundService.skipRequest(gamePin,userToken);
        logger.info("Skip Request from " + userToken);

    }
}