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
        advancedStatisticsGetDTO.setTotalPlayedGames(getTotalPlayedGames(userId));
        advancedStatisticsGetDTO.setTotalAnswersAnswered(getTotalAnswerAnswered(userId));
        advancedStatisticsGetDTO.setTotalPointsOverall(getTotalPointsOverall(userId));
        advancedStatisticsGetDTO.setTotalCorrectAndUniqueAnswers(getTotalCorrectAndUniqueAnswers(userId));
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
    Statistics: Total played games
     */
    private int getTotalPlayedGames(int userId){
        return gameRepository.findAllGamesByUserId(userId).size();
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
    Statistics: Total CorrectAndUnique, CorrectAndNotUnique, Wrong answers
     */
    private int getTotalCorrectAndUniqueAnswers(int userId) {
        return getTotalCountsOfAnswerTypes(userId).get(ScorePoint.CORRECT_UNIQUE);
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
