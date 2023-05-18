package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;

public class AdvancedStatisticGetDTO {

    int rank;
    int totalWins;
    int totalPlayedGames;
    int totalAnswersAnswered;
    int totalPointsOverall;
    int totalCorrectAndUniqueAnswers;
    String mostPlayedCategory;


    public int getRank() { return rank; }

    public void setRank(int rank) { this.rank = rank; }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalPlayedGames() {
        return totalPlayedGames;
    }

    public void setTotalPlayedGames(int totalPlayedGames) {
        this.totalPlayedGames = totalPlayedGames;
    }

    public int getTotalAnswersAnswered() {
        return totalAnswersAnswered;
    }

    public void setTotalAnswersAnswered(int totalAnswersAnswered) {
        this.totalAnswersAnswered = totalAnswersAnswered;
    }

    public int getTotalPointsOverall() {
        return totalPointsOverall;
    }

    public void setTotalPointsOverall(int totalPointsOverall) {
        this.totalPointsOverall = totalPointsOverall;
    }

    public int getTotalCorrectAndUniqueAnswers() {
        return totalCorrectAndUniqueAnswers;
    }

    public void setTotalCorrectAndUniqueAnswers(int totalCorrectAndUniqueAnswers) {
        this.totalCorrectAndUniqueAnswers = totalCorrectAndUniqueAnswers;
    }

    public String getMostPlayedCategory() {
        return mostPlayedCategory;
    }
    public void setMostPlayedCategory(String mostPlayedCategory) {
        this.mostPlayedCategory = mostPlayedCategory;
    }

}
