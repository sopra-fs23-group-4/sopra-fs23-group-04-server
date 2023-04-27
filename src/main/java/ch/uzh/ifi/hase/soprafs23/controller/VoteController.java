package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteGetDTO;
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
    public void saveVote(@PathVariable("gamePin") int gamePin,
                         @PathVariable("categoryName") String categoryName,
                         @RequestHeader("Authorization") String userToken,
                         @RequestBody Map<Long, String> votings){

        voteService.saveVote(gamePin, categoryName, userToken, votings);

    }

    @GetMapping("/games/{gamePin}/votings/{roundNumber}/{categoryName}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VoteGetDTO> getVotes(@PathVariable("gamePin") int gamePin,
                                     @PathVariable("roundNumber") int roundNumber,
                                     @PathVariable("categoryName") String categoryName,
                                     @RequestHeader("Authorization") String userToken){

        return voteService.getVotes(gamePin, roundNumber, categoryName, userToken);

    }

    @GetMapping("/voteOptions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public VoteOptionsGetDTO getVoteOptions(){

        return voteService.getVoteOptions();

    }
}
