package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

public class LeaderboardGetDTO {

    private String username;
    private int accumulatedScore;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAccumulatedScore() { return accumulatedScore; }

    public void setAccumulatedScore(int accumulatedScore) {
        this.accumulatedScore = accumulatedScore;
    }

}
