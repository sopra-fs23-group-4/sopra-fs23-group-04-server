package ch.uzh.ifi.hase.soprafs23.webSockets.DTO;

import ch.uzh.ifi.hase.soprafs23.entity.User;

public class ScoreboardEntryDTO {


    private User user;
    private int score;

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }

}
