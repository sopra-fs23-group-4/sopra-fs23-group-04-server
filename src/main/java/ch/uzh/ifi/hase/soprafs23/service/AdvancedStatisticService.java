package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameResult;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.AdvancedStatisticGetDTO;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

public class AdvancedStatisticService {

    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerRepository answerRepository;
    private final RoundRepository roundRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final ScoreCalculationService scoreCalculationService;

    public AdvancedStatisticService(@Qualifier("gameRepository")GameRepository gameRepository,
                                    @Qualifier("categoryRepository")CategoryRepository categoryRepository,
                                    @Qualifier("answerRepository")AnswerRepository answerRepository,
                                    @Qualifier("roundRepository")RoundRepository roundRepository,
                                    @Qualifier("voteRepository")VoteRepository voteRepository,
                                    @Qualifier("userRepository")UserRepository userRepository,
                                    ScoreCalculationService scoreCalculationService) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
        this.answerRepository = answerRepository;
        this.roundRepository = roundRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.scoreCalculationService = scoreCalculationService;
    }


    /*
    Statistics: Total Winner or Total Loser
     */
    private int getTotalWins(int userId) {
        List<Game> allUserGames = gameRepository.findAllGamesByUserId(userId);
        return getTotalCountWinOrLoss(allUserGames, userId, GameResult.WINNER);
    }

    private int getTotalLoss(int userId) {
        List<Game> allUserGames = gameRepository.findAllGamesByUserId(userId);
        return getTotalCountWinOrLoss(allUserGames, userId, GameResult.LOSER);
    }

    private int getTotalCountWinOrLoss(List<Game> games, int userId, GameResult condition) {
        int totalCount = 0;
        for (Game game : games) {
            GameResult result = checkIfWonOrLoss(game, userId);
            if (condition == result) {
                totalCount += 1;
            }
        }
        return totalCount;
    }

    // checkIfWonOrLoss: Return Winner if won and Looser if loss
    private GameResult checkIfWonOrLoss(Game game, int userId) {

        Map<User, Integer> userScores = scoreCalculationService.calculateUserTotalScoreInGame(game.getGamePin());

        // Sort the scores from high to low
        List<Map.Entry<User, Integer>> scoreList = new ArrayList<>(userScores.entrySet());
        scoreList.sort(Map.Entry.<User, Integer>comparingByValue().reversed());

        // Find the user and check if they are the first or last in the list
        for (int i = 0; i < scoreList.size(); i++) {
            if (scoreList.get(i).getKey().getId() == userId) {
                if (i == 0) {
                    return GameResult.WINNER;
                } else if (i == scoreList.size() - 1) {
                    return GameResult.LOSER;
                } else {
                    return GameResult.NEUTRAL;
                }
            }
        }

        // If the user is not found, return null
        return null;
    }

    /*
    Statistics: Percentage of Wins or Losses
     */
    private double getPercentageOfWin(int userId){
        int userTotalWins = getTotalWins(userId);
        int userTotalGames = gameRepository.findAllGamesByUserId(userId).size();

        return calculatePercentage(userTotalWins, userTotalGames);
    }

    private double getPercentageOfLoss(int userId){
        int userTotalLoss = getTotalLoss(userId);
        int userTotalGames = gameRepository.findAllGamesByUserId(userId).size();

        return calculatePercentage(userTotalLoss, userTotalGames);
    }

    private double calculatePercentage(int totalCount, int totalGames){
        if (totalGames == 0) {
            return 0;
        }
        return (double) totalCount / totalGames * 100; // Multiply by 100 to get a percentage
    }

    /*
    Statistics: Total played games
     */
    private int getTotalPlayedGames(int userId){
        return gameRepository.findAllGamesByUserId(userId).size();
    }

    /*
    Statistics: Total played rounds
     */
    private int getTotalPlayedRounds(int userId){
        List<Game> allGames = gameRepository.findAllGamesByUserId(userId);

        int totalRounds = 0;

        for (Game games: allGames){
            totalRounds += games.getRounds();
        }

        return totalRounds;
    }

    /*
    Statistics: Total answer answered
     */
    private int getTotalAnswerAnswered(int userId){
        return answerRepository.findAllByUser_Id(userId).size();
    }

    /*
    Statistics: Total points overall
     */
    private int getTotalPointsOverall(int userId) {
        List<Game> allUserGames = gameRepository.findAllGamesByUserId(userId);

        int totalPoints = 0;
        for (Game game : allUserGames) {
            Map<User, Integer> userScores = scoreCalculationService.calculateUserTotalScoreInGame(game.getGamePin());
            Optional<User> user = userRepository.findById(userId); // Assuming you have UserRepository

            if(userScores.containsKey(user)) {
                totalPoints += userScores.get(user);
            }
        }

        return totalPoints;
    }

    /*
    Statistics: Average amount of rounds played per game
     */
    private double getAvgPlayedRoundsPerGame(int userId){
        List<Game> allUserGames = gameRepository.findAllGamesByUserId(userId);

        if (allUserGames.isEmpty()) {
            return 0; // Return 0 if the user has never played a game
        }

        List<Integer> roundsPerGame  = new ArrayList<>();
        for (Game games: allUserGames){
            roundsPerGame.add(games.getRounds());
        }

        return calculateAverage(roundsPerGame);

    }

    public double calculateAverage(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty.");
        }

        return numbers.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    /*
    Statistics: Total CorrectAndUnique, CorrectAndNotUnique, Wrong answers
     */
    private int getTotalCorrectAndUniqueAnswers(int userId) {
        return getTotalCountOfGivenVoteOption(userId, VoteOption.CORRECT_UNIQUE);
    }

    private int getTotalCorrectAndNotUniqueAnswers(int userId) {
        return getTotalCountOfGivenVoteOption(userId, VoteOption.CORRECT_NOT_UNIQUE);
    }

    private int getTotalWrongAnswers(int userId) {
        return getTotalCountOfGivenVoteOption(userId, VoteOption.WRONG);
    }

    private int getTotalCountOfGivenVoteOption(int userId, VoteOption voteOption) {
        List<Answer> userAnswers = answerRepository.findAllByUser_Id(userId);

        int voteOptionCount = 0;
        for (Answer answer : userAnswers) {
            List<Vote> votesForAnswer = voteRepository.findByAnswer(answer);
            for (Vote vote : votesForAnswer) {
                if (vote.getVotedOption().equals(voteOption)) {
                    voteOptionCount++;
                }
            }
        }
        return voteOptionCount;
    }

    /*
    Statistics: Percentage of correct answers per game
     */
    private double getPercentOfCorrectPerGame(int userId){
        int totalGames = gameRepository.findAllGamesByUserId(userId).size();
        int totalCorrectAnswer = getTotalCorrectAndUniqueAnswers(userId)
                + getTotalCorrectAndNotUniqueAnswers(userId);

        return calculatePercentage(totalCorrectAnswer, totalGames);
    }

    /*
    Statistics: Most played and best played categories
     */
    private Category getMostPlayedCategory(int userId) {
        List<Game> allUserGames = gameRepository.findAllGamesByUserId(userId);

        Map<Category, Integer> categoryCount = new HashMap<>();

        for (Game game : allUserGames) {
            for (Category category : game.getCategories()) {
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
        }

        // Find the category with the maximum value
        return Collections.max(categoryCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

        public AdvancedStatisticGetDTO getAdvancedUserStatistic(int userId) {


        AdvancedStatisticGetDTO advancedStatisticsGetDTO = new AdvancedStatisticGetDTO();

        advancedStatisticsGetDTO.setTotalWins(getTotalWins(userId));
        advancedStatisticsGetDTO.setTotalLoss(getTotalLoss(userId));
        advancedStatisticsGetDTO.setPercentOfWins(getPercentageOfWin(userId));
        advancedStatisticsGetDTO.setPercentOfLoss(getPercentageOfLoss(userId));
        advancedStatisticsGetDTO.setTotalPlayedGames(getTotalPlayedGames(userId));
        advancedStatisticsGetDTO.setTotalPlayedRounds(getTotalPlayedRounds(userId));
        advancedStatisticsGetDTO.setTotalAnswersAnswered(getTotalAnswerAnswered(userId));
        advancedStatisticsGetDTO.setTotalPointsOverall(getTotalPointsOverall(userId));
        advancedStatisticsGetDTO.setAvgPlayedRoundsPerGames(getAvgPlayedRoundsPerGame(userId));
        advancedStatisticsGetDTO.setTotalCorrectAndUniqueAnswers(getTotalCorrectAndUniqueAnswers(userId));
        advancedStatisticsGetDTO.setTotalCorrectAndNotUniqueAnswers(getTotalCorrectAndNotUniqueAnswers(userId));
        advancedStatisticsGetDTO.setTotalWrongAnswers(getTotalWrongAnswers(userId));
        advancedStatisticsGetDTO.setPercentOfCorrectPerGame(getPercentOfCorrectPerGame(userId));
        advancedStatisticsGetDTO.setMostPlayedCategory(getMostPlayedCategory(userId));


        return advancedStatisticsGetDTO;
    }

}
