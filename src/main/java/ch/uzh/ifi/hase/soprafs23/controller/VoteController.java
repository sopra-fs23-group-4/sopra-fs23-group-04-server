package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VoteController {

    private final VoteService voteService;

    VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/games/{gamePin}/votings/{answerId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void saveVote(@PathVariable("gamePin") int gamePin,
                         @PathVariable("answerId") Long answerId,
                         @RequestHeader("Authorization") String userToken,
                         @RequestBody List<String> newVotes){

        voteService.saveVote(gamePin, answerId, userToken, newVotes);
    }
}
