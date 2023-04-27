package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.repository.VoteRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VoteService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(@Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("answerRepository") AnswerRepository answerRepository,
                       @Qualifier("voteRepository") VoteRepository voteRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
    }

    public void saveVote(int gamePin, String categoryName, String userToken, Map<Long, String> votings) {

        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExists(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);

        checkIfUserIsInGame(game, user);

        for (Map.Entry<Long, String> voting : votings.entrySet()) {

            Long answerId = voting.getKey();
            Answer answer = getAnswerById(answerId);

            checkIfCategoryMatches(answer, categoryName);

            String votingString = voting.getValue();

            checkIfAnswerAndVotingExists(answer, user);

            saveVoting(answer, user, votingString);

        }
    }

    public VoteOptionsGetDTO getVoteOptions() {
        VoteOptionsGetDTO voteOptionsGetDTO = new VoteOptionsGetDTO();
        List<String> voteOptions = new ArrayList<>();
        for (VoteOption voteOption : VoteOption.values()) {
            voteOptions.add(voteOption.name());
        }
        voteOptionsGetDTO.setVoteOptions(voteOptions);
        return voteOptionsGetDTO;
    }

    private void checkIfGameExists(Game game) {
        String errorMessage = "Game does not exist. Please try again with a different game!";

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
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

    private void checkIfUserIsInGame(Game game, User user) {
        List<User> users = game.getUsers();

        String errorMessage = "User is not part of this game.";

        if(!users.contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }

    private void checkIfCategoryMatches(Answer answer, String categoryName) {

        String errorMessage = "The category of voting and the answers don't match.";

        System.out.println("\n---------------------------------");
        System.out.println(answer.getCategory().getName());
        System.out.println(categoryName);
        System.out.println("---------------------------------\n");

        if(!answer.getCategory().getName().equals(categoryName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }
    private Answer getAnswerById(Long answerId) {
        return answerRepository.findById(answerId);
    }

    private void checkIfAnswerAndVotingExists(Answer answer, User user) {

        checkIfAnswerExists(answer);

        checkIfVotingExists(user, answer);
    }

    private void checkIfAnswerExists(Answer answer) {

        String errorMessage = "This answer does not exist.";

        if (answer == null) {
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

    private void saveVoting(Answer answer, User user, String votingString) {
        Vote newVote = new Vote();
        newVote.setAnswer(answer);
        newVote.setUser(user);
        newVote = setVoteOption(newVote, votingString);
        voteRepository.saveAndFlush(newVote);
    }

    private Vote setVoteOption(Vote newVote, String vote) {
        String errorMessage = "At least one of the votes is invalid!";

        try {
            newVote.setVotedOption(VoteOption.valueOf(vote));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }

        return newVote;
    }
}
