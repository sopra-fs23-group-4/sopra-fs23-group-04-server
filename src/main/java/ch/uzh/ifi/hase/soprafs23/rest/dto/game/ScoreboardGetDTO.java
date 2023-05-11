package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

public class ScoreboardGetDTO {


    private String username;
    private int score;

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }

}