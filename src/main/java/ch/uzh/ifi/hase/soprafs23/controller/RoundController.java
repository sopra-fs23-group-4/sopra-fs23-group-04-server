package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoundController {

    private final RoundService roundService;

    RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @PostMapping("/games/rounds/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createAllRounds(@RequestBody Game game) {

        roundService.createAllRounds(game);
    }
}
