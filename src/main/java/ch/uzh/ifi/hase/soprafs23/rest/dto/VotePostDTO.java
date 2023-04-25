package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.List;

public class VotePostDTO {
    List<String> votes;

    public List<String> getVotes() {
        return this.votes;
    }

    public void setVotes(List<String> votes) {
        this.votes = votes;
    }

}
