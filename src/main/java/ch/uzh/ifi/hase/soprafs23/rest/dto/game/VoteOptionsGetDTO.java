package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

import java.util.List;

public class VoteOptionsGetDTO {

    private List<String> voteOptions;


    public List<String> getVoteOptions() {
        return voteOptions;
    }

    public void setVoteOptions(List<String> voteOptions) {
        this.voteOptions = voteOptions;
    }
}
