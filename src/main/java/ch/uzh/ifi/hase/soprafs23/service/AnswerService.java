package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.OPEN;
import static ch.uzh.ifi.hase.soprafs23.constant.RoundStatus.FINISHED;

@Service
@Transactional
public class AnswerService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final RoundRepository roundRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public AnswerService(@Qualifier("gameRepository") GameRepository gameRepository,
                         @Qualifier("userRepository") UserRepository userRepository,
                         @Qualifier("roundRepository") RoundRepository roundRepository,
                         @Qualifier("answerRepository") AnswerRepository answerRepository,
                         @Qualifier("voteRepository") VoteRepository voteRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.roundRepository = roundRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
    }

    public void saveAnswers(int gamePin, String userToken, Long roundNumber, Map<String, String> answers) {
        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExistsAndIsOpen(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExistsAndIsFinished(round);

        checkIfAnswerExists(round, user);

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String categoryName = entry.getKey();
            String answer = entry.getValue();
            Answer newAnswer = new Answer();
            newAnswer.setRound(round);
            newAnswer.setUser(user);
            newAnswer.setAnswer(answer);
            newAnswer.setAnswerCategory(categoryName);
            newAnswer = answerRepository.save(newAnswer);
        }
        answerRepository.flush();
    }

    public ScorePoint calculateScorePoint(Answer answer) {
        List<Vote> votes = voteRepository.findByAnswer(answer);
        int correctUniqueVotes = 0;
        int correctNotUniqueVotes = 0;
        int incorrectVotes = 0;

        for (Vote vote : votes) {
            VoteOption votedOption = vote.getVotedOption();
            switch (votedOption) {
                case CORRECT_UNIQUE -> correctUniqueVotes++;
                case CORRECT_NOT_UNIQUE -> correctNotUniqueVotes++;
                case INCORRECT -> incorrectVotes++;
            }
        }

        int correctVotes = correctUniqueVotes + correctNotUniqueVotes;

        if (correctVotes > incorrectVotes) {
            return (correctNotUniqueVotes > 0) ? ScorePoint.CORRECT_NOT_UNIQUE : ScorePoint.CORRECT_UNIQUE;
        } else {
            return ScorePoint.INCORRECT;
        }
    }

    private void checkIfGameExistsAndIsOpen(Game game) {

        String errorMessage = "Game does not exist or is not open anymore." +
                "Please try again with a different game!";
        if (game == null || !game.getStatus().equals(OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfUserExists(User user) {

        String errorMessage = "User does not exist." +
                "Please register before playing!";
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfRoundExistsAndIsFinished(Round round) {

        String errorMessage = "Round does not exist or is not finished yet.";

        if (round == null || !round.getStatus().equals(FINISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfAnswerExists(Round round, User user) {

        List<Answer> answers = answerRepository.findByRoundAndUser(round, user);

        String errorMessage = "These Answers have already been saved.";

        if (answers.size() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
}
