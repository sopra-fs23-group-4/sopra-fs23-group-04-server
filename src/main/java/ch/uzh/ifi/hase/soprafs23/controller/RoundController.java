package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.LetterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoundController {

    private final RoundService roundService;
    private final SimpMessagingTemplate messagingTemplate;

    RoundController(RoundService roundService,
                    SimpMessagingTemplate messagingTemplate) {
        this.roundService = roundService;
        this.messagingTemplate=messagingTemplate;
    }

    @PutMapping("/games/{gamePin}/{roundNumber}/start")
    public void gameStart(@PathVariable("gamePin") int gamePin,
                          @PathVariable("roundNumber") int roundNumber) {

        LetterDTO letterDTO = roundService.startRound(gamePin, roundNumber);

        messagingTemplate.convertAndSend("/topic/lobbies/" +gamePin, letterDTO);
    }

    @PutMapping("/games/{gamePin}/{roundNumber}/end")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void endRound(@PathVariable("gamePin") int gamePin,
                         @PathVariable("roundNumber") int roundNumber,
                         @RequestHeader("Authorization") String userToken) {

        roundService.endRound(gamePin, userToken, roundNumber);

        String type="end";
        messagingTemplate.convertAndSend("/topic/games/" + gamePin + "/rounds", type);
    }
}
