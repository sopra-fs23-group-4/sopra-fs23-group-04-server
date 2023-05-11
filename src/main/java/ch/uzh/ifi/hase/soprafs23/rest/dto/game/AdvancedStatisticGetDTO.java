package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;

public class AdvancedStatisticGetDTO {

    int totalWins;
    int totalLoss;
    double percentageOfWins;
    double percentageOfLoss;
    int totalPlayedGames;
    int totalPlayedRounds;
    int totalAnswersAnswered;
    int totalPointsOverall;
    double avgPlayedRoundsPerGames;
    int totalCorrectAndUniqueAnswers;
    int totalCorrectAndNotUniqueAnswers;
    int totalWrongAnswers;
    double percentOfCorrectAnswersPerGame;
    Category mostPlayedCategory;



    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalLoss() {
        return totalLoss;
    }

    public void setTotalLoss(int totalLoss) {
        this.totalLoss = totalLoss;
    }

    public double getPercentOfWins() {
        return percentageOfWins;
    }

    public void setPercentOfWins(double percentOfWins) {
        this.percentageOfWins = percentOfWins;
    }

    public double getPercentOfLoss() {
        return percentageOfLoss;
    }

    public void setPercentOfLoss(double percentOfLoss) {
        this.percentageOfLoss = percentOfLoss;
    }


    public int getTotalPlayedGames() {
        return totalPlayedGames;
    }

    public void setTotalPlayedGames(int totalPlayedGames) {
        this.totalPlayedGames = totalPlayedGames;
    }

    public int getTotalPlayedRounds() {
        return totalPlayedRounds;
    }

    public void setTotalPlayedRounds(int totalPlayedRounds) {
        this.totalPlayedRounds = totalPlayedRounds;
    }

    public int getTotalAnswersAnswered() {
        return totalAnswersAnswered;
    }

    public void setTotalAnswersAnswered(int totalAnswersAnswered) {
        this.totalAnswersAnswered = totalAnswersAnswered;
    }

    public double getAvgPlayedRoundsPerGames() {
        return avgPlayedRoundsPerGames;
    }

    public void setAvgPlayedRoundsPerGames(double avgPlayedRoundsPerGames) {
        this.avgPlayedRoundsPerGames = avgPlayedRoundsPerGames;
    }

    public int getTotalPointsOverall() {
        return totalPointsOverall;
    }

    public void setTotalPointsOverall(int totalPointsOverall) {
        this.totalPointsOverall = totalPointsOverall;
    }

    public double getPercentOfCorrectPerGame() {
        return percentOfCorrectAnswersPerGame;
    }

    public void setPercentOfCorrectPerGame(double percentOfCorrectPerGame) {
        this.percentOfCorrectAnswersPerGame = percentOfCorrectPerGame;
    }


    public int getTotalCorrectAndUniqueAnswers() {
        return totalCorrectAndUniqueAnswers;
    }

    public void setTotalCorrectAndUniqueAnswers(int totalCorrectAndUniqueAnswers) {
        this.totalCorrectAndUniqueAnswers = totalCorrectAndUniqueAnswers;
    }

    public int getTotalCorrectAndNotUniqueAnswers() {
        return totalCorrectAndNotUniqueAnswers;
    }

    public void setTotalCorrectAndNotUniqueAnswers(int totalCorrectAndNotUniqueAnswers) {
        this.totalCorrectAndNotUniqueAnswers = totalCorrectAndNotUniqueAnswers;
    }

    public int getTotalWrongAnswers() {
        return totalWrongAnswers;
    }

    public void setTotalWrongAnswers(int totalWrongAnswers) {
        this.totalWrongAnswers = totalWrongAnswers;
    }


    public Category getMostPlayedCategory() {
        return mostPlayedCategory;
    }

    public void setMostPlayedCategory(Category mostPlayedCategory) {
        this.mostPlayedCategory = mostPlayedCategory;
    }

}
