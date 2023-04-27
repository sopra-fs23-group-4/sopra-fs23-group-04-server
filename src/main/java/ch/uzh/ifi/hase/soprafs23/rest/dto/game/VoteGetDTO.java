package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;

public class VoteGetDTO {
    private String username;
    private String answerString;
    private int numberOfUnique;
    private int numberOfNotUnique;
    private int numberOfWrong;
    private ScorePoint points;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
    }

    public int getNumberOfUnique() {
        return numberOfUnique;
    }

    public void setNumberOfUnique(int numberOfUnique) {
        this.numberOfUnique = numberOfUnique;
    }

    public int getNumberOfNotUnique() {
        return numberOfNotUnique;
    }

    public void setNumberOfNotUnique(int numberOfNotUnique) {
        this.numberOfNotUnique = numberOfNotUnique;
    }

    public int getNumberOfWrong() {
        return numberOfWrong;
    }

    public void setNumberOfWrong(int numberOfWrong) {
        this.numberOfWrong = numberOfWrong;
    }

    public ScorePoint getPoints() {
        return points;
    }

    public void setPoints(ScorePoint points) {
        this.points = points;
    }
}
