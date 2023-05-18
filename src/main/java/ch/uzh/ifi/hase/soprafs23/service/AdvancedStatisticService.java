package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameResult;
import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.AdvancedStatisticGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.LeaderboardGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AdvancedStatisticService {

    private final GameRepository gameRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final ScoreCalculationService scoreCalculationService;
    private final GameService gameService;

    @Autowired
    public AdvancedStatisticService(@Qualifier("gameRepository")GameRepository gameRepository,
                                    @Qualifier("answerRepository")AnswerRepository answerRepository,
                                    @Qualifier("voteRepository")VoteRepository voteRepository,
                                    @Qualifier("userRepository")UserRepository userRepository,
                                    ScoreCalculationService scoreCalculationService,
                                    GameService gameService) {
        this.gameRepository = gameRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.scoreCalculationService = scoreCalculationService;
        this.gameService = gameService;
    }

    public AdvancedStatisticGetDTO getAdvancedUserStatistic(int userId) {

        AdvancedStatisticGetDTO advancedStatisticsGetDTO = new AdvancedStatisticGetDTO();

        advancedStatisticsGetDTO.setRank(getRank(userId));
        advancedStatisticsGetDTO.setTotalWins(getTotalWins(userId));
        //advancedStatisticsGetDTO.setTotalLoss(getTotalLoss(userId));
        //advancedStatisticsGetDTO.setPercentOfWins(getPercentageOfWin(userId));
        //advancedStatisticsGetDTO.setPercentOfLoss(getPercentageOfLoss(userId));
        advancedStatisticsGetDTO.setTotalPlayedGames(getTotalPlayedGames(userId));
        //advancedStatisticsGetDTO.setTotalPlayedRounds(getTotalPlayedRounds(userId));
        advancedStatisticsGetDTO.setTotalAnswersAnswered(getTotalAnswerAnswered(userId));
        advancedStatisticsGetDTO.setTotalPointsOverall(getTotalPointsOverall(userId));
        //advancedStatisticsGetDTO.setAvgPlayedRoundsPerGames(getAvgPlayedRoundsPerGame(userId));
        advancedStatisticsGetDTO.setTotalCorrectAndUniqueAnswers(getTotalCorrectAndUniqueAnswers(userId));
        //advancedStatisticsGetDTO.setTotalCorrectAndNotUniqueAnswers(getTotalCorrectAndNotUniqueAnswers(userId));
        //advancedStatisticsGetDTO.setTotalWrongAnswers(getTotalWrongAnswers(userId));
        //advancedStatisticsGetDTO.setPercentOfCorrectPerGame(getPercentOfCorrectPerGame(userId));
        advancedStatisticsGetDTO.setMostPlayedCategory(getMostPlayedCategory(userId));


        return advancedStatisticsGetDTO;
    }

    /*
    Statistics: Rank of a player
     */
    private int getRank(int userId) {
        // Get the user
        User user = userRepository.findById(userId).orElse(null);

        // If user is not found, return -1
        if (user == null) {
            return -1;
        }

        // Get the leaderboard
        List<LeaderboardGetDTO> leaderboard = gameService.getLeaderboard();

        // Initialize variables to keep track of current rank and score
        int rank = 1;
        int sameScoreCount = 0;
        int lastScore = -1; // Assuming scores are non-negative

        // Find the rank of the specified user
        for (LeaderboardGetDTO entry : leaderboard) {
            int currentScore = entry.getAccumulatedScore();

            // If score is same as previous, increment same score count
            if (currentScore == lastScore) {
                sameScoreCount++;
            } else {
                // If score is different, increment rank by number of users with the same score, and reset same score count
                rank += sameScoreCount;
                sameScoreCount = 1;
                lastScore = currentScore;
            }

            // If the current entry is the specified user, return the rank
            if (entry.getUsername().equals(user.getUsername())) {
                return rank;
            }
        }

        // If the user is not found in the leaderboard, return -1
        return -1;
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
        Map<User, Integer> userScores = scoreCalculationService.calculateUserScores(game.getGamePin());

        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        int userScore = -1;

        for (Map.Entry<User, Integer> entry : userScores.entrySet()) {
            int score = entry.getValue();
            if (entry.getKey().getId() == userId) {
                userScore = score;
            }
            maxScore = Math.max(maxScore, score);
            minScore = Math.min(minScore, score);
        }

        if (userScore == maxScore) {
            return GameResult.WINNER;
        } else if (userScore == minScore) {
            return GameResult.LOSER;
        } else if (userScore != -1) {
            return GameResult.NEUTRAL;
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
            Map<User, Integer> userScores = scoreCalculationService.calculateUserScores(game.getGamePin());
            User user = userRepository.findById(userId).orElse(null);

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
        return getTotalCountsOfAnswerTypes(userId).get(ScorePoint.CORRECT_UNIQUE);
    }

    private int getTotalCorrectAndNotUniqueAnswers(int userId) {
        return getTotalCountsOfAnswerTypes(userId).get(ScorePoint.CORRECT_NOT_UNIQUE);
    }

    private int getTotalWrongAnswers(int userId) {
        return getTotalCountsOfAnswerTypes(userId).get(ScorePoint.INCORRECT);
    }

    private Map<ScorePoint, Integer> getTotalCountsOfAnswerTypes(int userId) {
        List<Answer> userAnswers = answerRepository.findAllByUser_Id(userId);

        Map<ScorePoint, Integer> answerTypeCounts = new HashMap<>();
        answerTypeCounts.put(ScorePoint.CORRECT_UNIQUE, 0);
        answerTypeCounts.put(ScorePoint.CORRECT_NOT_UNIQUE, 0);
        answerTypeCounts.put(ScorePoint.INCORRECT, 0);

        for (Answer answer : userAnswers) {
            List<Vote> votesForAnswer = voteRepository.findByAnswer(answer);

            Map<VoteOption, Integer> voteCounts = new HashMap<>();
            voteCounts.put(VoteOption.CORRECT_UNIQUE, 0);
            voteCounts.put(VoteOption.CORRECT_NOT_UNIQUE, 0);
            voteCounts.put(VoteOption.WRONG, 0);
            voteCounts.put(VoteOption.NO_VOTE, 0);

            for (Vote vote : votesForAnswer) {
                VoteOption voteOption = vote.getVotedOption();
                voteCounts.put(voteOption, voteCounts.get(voteOption) + 1);
            }

            ScorePoint answerType;
            if ((voteCounts.get(VoteOption.CORRECT_UNIQUE) + voteCounts.get(VoteOption.CORRECT_NOT_UNIQUE)) >= voteCounts.get(VoteOption.WRONG)) {
                answerType = voteCounts.get(VoteOption.CORRECT_UNIQUE) >= voteCounts.get(VoteOption.CORRECT_NOT_UNIQUE) ? ScorePoint.CORRECT_UNIQUE : ScorePoint.CORRECT_NOT_UNIQUE;
            } else {
                answerType = ScorePoint.INCORRECT;
            }

            answerTypeCounts.put(answerType, answerTypeCounts.get(answerType) + 1);
        }

        return answerTypeCounts;
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
    private String getMostPlayedCategory(int userId) {
        List<Game> allUserGames = gameRepository.findAllGamesByUserId(userId);

        Map<String, Integer> categoryCount = new HashMap<>();

        for (Game game : allUserGames) {
            for (Category category : game.getCategories()) {
                String categoryName = category.getName();
                categoryCount.put(categoryName, categoryCount.getOrDefault(categoryName, 0) + 1);
            }
        }

        if (!categoryCount.isEmpty()) {
            // Get the max count
            int maxCount = Collections.max(categoryCount.values());

            // Collect all categories with the max count
            List<String> maxCategories = categoryCount.entrySet().stream()
                    .filter(entry -> entry.getValue() == maxCount)
                    .map(Map.Entry::getKey)
                    .toList();

            // Randomly select one of the categories with the max count
            Random rand = new Random();
            return maxCategories.get(rand.nextInt(maxCategories.size()));
        } else {
            // If categoryCount is empty, return null or handle it in the way that is most suitable for your application.
            return null;
        }
    }

}
