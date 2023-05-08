package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

public class VoteGetDTO {
    private String username;
    private String answerString;
    private int numberOfUnique;
    private int numberOfNotUnique;
    private int numberOfWrong;
    private int numberOfNoVote;
    private int points;

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

    public int getNumberOfNoVote() {
        return numberOfNoVote;
    }

    public void setNumberOfNoVote(int numberOfNoVote) {
        this.numberOfNoVote = numberOfNoVote;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
