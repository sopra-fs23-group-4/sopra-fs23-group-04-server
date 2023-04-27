package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class VoteController {

    private final VoteService voteService;

    VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/games/{gamePin}/votings/{categoryName}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void saveVote(@PathVariable("gamePin") int gamePin,
                         @PathVariable("categoryName") String categoryName,
                         @RequestHeader("Authorization") String userToken,
                         @RequestBody Map<Long, String> votings){

        voteService.saveVote(gamePin, categoryName, userToken, votings);
    }

    @GetMapping("/voteOptions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public VoteOptionsGetDTO getVoteOptions(){

        return voteService.getVoteOptions();
    }
}
