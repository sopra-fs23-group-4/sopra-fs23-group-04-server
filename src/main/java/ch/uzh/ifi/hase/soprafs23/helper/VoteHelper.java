package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class VoteHelper {

    private VoteHelper() {}
    public static void checkIfVotingAlreadyExists(Vote vote) {

        String errorMessage = "This voting already exists.";

        if (vote != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
    public static VoteOptionsGetDTO getVoteOptions() {

        VoteOptionsGetDTO voteOptionsGetDTO = new VoteOptionsGetDTO();
        List<String> voteOptions = new ArrayList<>();

        for (VoteOption voteOption : VoteOption.values()) {
            voteOptions.add(voteOption.name());
        }

        voteOptionsGetDTO.setVoteOptions(voteOptions);

        return voteOptionsGetDTO;
    }
}
