package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

import ch.uzh.ifi.hase.soprafs23.entity.User;

public class WinnerDTO {

    private User user;
    private Long score;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}