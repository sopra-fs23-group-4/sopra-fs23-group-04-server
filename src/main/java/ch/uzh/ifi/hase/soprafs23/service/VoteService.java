package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class VoteService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;
    private final AnswerService answerService;

    @Autowired
    public VoteService(@Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("answerRepository") AnswerRepository answerRepository,
                       @Qualifier("voteRepository") VoteRepository voteRepository,
                       @Qualifier("answerService") AnswerService answerService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
        this.answerService = answerService;
    }

    public void saveVote(int gamePin, Long answerId, String userToken, List<String> votingStrings) {

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);

        Answer answer = answerRepository.findById(answerId);
        checkIfAnswerExists(answer);

        checkIfVotingExists(user, answer);

        for (String votingString : votingStrings) {
            Vote newVote = new Vote();
            newVote.setAnswer(answer);
            newVote.setUser(user);
            setVoteOption(newVote, votingString);
            voteRepository.save(newVote);
        }
        voteRepository.flush();

        // Calculate the score points for the answer after votes have been saved
        ScorePoint scorePoint = answerService.calculateScorePoint(answer);
        answer.setScorePoint(scorePoint);
        answerRepository.flush();
    }

    private void checkIfUserExists(User user) {

        String errorMessage = "User does not exist." +
                "Please register before playing!";
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfAnswerExists(Answer answer) {

        String errorMessage = "This answer does not exist.";

        if (answer == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void setVoteOption(Vote newVote, String vote) {
        String errorMessage = "At least one of the votes is invalid!";

        try {
            newVote.setVotedOption(VoteOption.valueOf(vote));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfVotingExists(User user, Answer answer) {

        List<Vote> votes = voteRepository.findByUserAndAnswer(user, answer);

        String errorMessage = "This Voting has already been saved.";

        if (votes.size() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
}
