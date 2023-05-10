package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

public class WinnerGetDTO {

    private String username;
    private int score;
    private String quote;


    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }

    public String getQuote() { return quote; }

    public void setQuote(String quote) { this.quote = quote; }
}