package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class VotingEndDTO {
    public static final String type ="votingEnd";

    private int round;
    private int categoryIndex;

    public int getCategoryIndex() {
        return categoryIndex;
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
