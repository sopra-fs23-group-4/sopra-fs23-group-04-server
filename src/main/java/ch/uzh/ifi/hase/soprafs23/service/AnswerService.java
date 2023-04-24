package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    public AnswerService(@Qualifier("gameRepository") GameRepository gameRepository,
                         @Qualifier("userRepository") UserRepository userRepository,
                         @Qualifier("roundRepository") RoundRepository roundRepository,
                         @Qualifier("answerRepository") AnswerRepository answerRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.roundRepository = roundRepository;
        this.answerRepository = answerRepository;
    }

    public void saveAnswers(int gamePin, String userToken, Long roundNumber, Map<String, String> answers) {
        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExistsAndIsOpen(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExistsAndIsFinished(round);

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String categoryName = entry.getKey();
            String answer = entry.getValue();
            Answer newAnswer = new Answer();
            newAnswer.setRound(round);
            newAnswer.setUser(user);
            newAnswer.setAnswer(answer);
            newAnswer = answerRepository.save(newAnswer);
        }
        answerRepository.flush();
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
}
