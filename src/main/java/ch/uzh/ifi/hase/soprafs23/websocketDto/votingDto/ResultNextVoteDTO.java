package ch.uzh.ifi.hase.soprafs23.websocketDto.votingDto;

public class ResultNextVoteDTO {
    public static final String type ="resultNextVote";

    private int round;
    private int categoryIndex;

    public int getCategoryIndex() {
        return categoryIndex;
    }
    public String getType() {
        return type;
    }

    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
