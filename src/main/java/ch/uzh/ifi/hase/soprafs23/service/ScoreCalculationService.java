package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ScoreCalculationService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Map<User, Integer> calculateUserTotalScoreInGame(int gamePin) {
        Game game = gameRepository.findByGamePin(gamePin);
        List<Round> rounds = roundRepository.findByGame(game);

        Map<User, Integer> userScores = new HashMap<>();

        for (Round round : rounds) {
            List<Answer> answers = answerRepository.findByRound(round);
            for (Answer answer : answers) {
                List<Vote> votesForAnswer = voteRepository.findByAnswer(answer);
                int answerScore = calculateScoreOfAnswer(votesForAnswer);
                User user = answer.getUser();

                // If user already has a score, add to it, else put the current answer score
                userScores.merge(user, answerScore, Integer::sum);
            }
        }

        return userScores;
    }

    public int calculateScoreOfAnswer(List<Vote> votesForAnswer) {
        int numberOfUnique = 0;
        int numberOfNotUnique = 0;
        int numberOfWrong = 0;

        for (Vote vote : votesForAnswer) {
            if (vote.getVotedOption().equals(VoteOption.CORRECT_UNIQUE)) {
                numberOfUnique++;
            } else if (vote.getVotedOption().equals(VoteOption.CORRECT_NOT_UNIQUE)) {
                numberOfNotUnique++;
            } else if (vote.getVotedOption().equals(VoteOption.WRONG)) {
                numberOfWrong++;
            }
        }

        return calculatePoints(numberOfUnique, numberOfNotUnique, numberOfWrong);
    }

    private int calculatePoints(int numberOfUnique, int numberOfNotUnique, int numberOfWrong) {
        int numberOfCorrect = numberOfUnique + numberOfNotUnique;

        if (numberOfCorrect >= numberOfWrong) {
            if (numberOfUnique >= numberOfNotUnique) {
                return ScorePoint.CORRECT_UNIQUE.getPoints();
            } else {
                return ScorePoint.CORRECT_NOT_UNIQUE.getPoints();
            }
        } else {
            return ScorePoint.INCORRECT.getPoints();
        }
    }
}