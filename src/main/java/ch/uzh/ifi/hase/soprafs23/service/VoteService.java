package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.*;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static ch.uzh.ifi.hase.soprafs23.constant.VoteOption.NO_VOTE;
import static ch.uzh.ifi.hase.soprafs23.helper.GameHelper.*;
import static ch.uzh.ifi.hase.soprafs23.helper.RoundHelper.*;
import static ch.uzh.ifi.hase.soprafs23.helper.CategoryHelper.*;
import static ch.uzh.ifi.hase.soprafs23.helper.AnswerHelper.*;
import static ch.uzh.ifi.hase.soprafs23.helper.VoteHelper.*;
import static ch.uzh.ifi.hase.soprafs23.helper.UserHelper.*;

@Service
@Transactional
public class VoteService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;
    private final CategoryRepository categoryRepository;
    private final RoundRepository roundRepository;

    @Autowired
    public VoteService(@Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("answerRepository") AnswerRepository answerRepository,
                       @Qualifier("voteRepository") VoteRepository voteRepository,
                       @Qualifier("categoryRepository") CategoryRepository categoryRepository,
                       @Qualifier("roundRepository") RoundRepository roundRepository,
                       WebSocketService webSocketService) {

        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
        this.categoryRepository = categoryRepository;
        this.roundRepository=roundRepository;
    }



    public void saveVote(int gamePin, String categoryName, String userToken, Map<Integer, String> votings) {

        Game game = gameRepository.findByGamePin(gamePin);
        User user = userRepository.findByToken(userToken);

        checkIfGameExists(game);
        checkIfUserExists(user);

        checkIfUserIsInGame(game, user);

        for (Map.Entry<Integer, String> voting : votings.entrySet()) {

            int answerId = voting.getKey();
            Answer answer = getAnswerById(answerId);

            checkIfCategoryMatches(answer, categoryName);

            String votingString = voting.getValue();

            Vote vote = voteRepository.findByUserAndAnswer(user, answer);

            checkIfAnswerAndVoteExists(answer, vote);

            saveVoting(answer, user, votingString);

        }
    }

    public List<VoteGetDTO> getVotes(int gamePin, int roundNumber, String categoryName, String userToken) {

        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        Category category = categoryRepository.findByName(categoryName);
        User requestUser = userRepository.findByToken(userToken);

        checkIfGameExists(game);
        checkIfRoundExists(round);
        checkIfCategoryExists(category);
        checkIfUserExists(requestUser);
        checkIfUserIsInGame(game, requestUser);

        List<User> users = game.getUsers();

        return getVoteGetDTOList(round, category, users);

    }

    public static VoteOptionsGetDTO getVoteOptions() {

        VoteOptionsGetDTO voteOptionsGetDTO = new VoteOptionsGetDTO();
        List<String> voteOptions = new ArrayList<>();

        for (VoteOption voteOption : VoteOption.values()) {
            voteOptions.add(voteOption.name());
        }

        voteOptionsGetDTO.setVoteOptions(voteOptions);

        return voteOptionsGetDTO;
    }

    private List<VoteGetDTO> getVoteGetDTOList(Round round, Category category, List<User> users) {

        List<VoteGetDTO> voteGetDTOList = new ArrayList<>();

        for (User user : users) {

            Answer answer = answerRepository.findByRoundAndCategoryAndUser(round, category, user);
            checkIfAnswerExists(answer);

            List<User> allUsersFiltered = new ArrayList<>(users);
            allUsersFiltered.remove(user);

            VoteGetDTO newVoteGetDTO = createVoteGetDTO(user.getUsername(), allUsersFiltered, answer);
            voteGetDTOList.add(newVoteGetDTO);
        }

        return voteGetDTOList;

    }

    private VoteGetDTO createVoteGetDTO(String username, List<User> allUsersFiltered, Answer answer) {
        int numberOfUnique = 0;
        int numberOfNotUnique = 0;
        int numberOfWrong = 0;
        int numberOfNoVote = 0;

        VoteGetDTO voteGetDTO = new VoteGetDTO();
        voteGetDTO.setUsername(username);
        voteGetDTO.setAnswerString(answer.getAnswerString());

        for (User user : allUsersFiltered) {

            Vote vote = voteRepository.findByUserAndAnswer(user, answer);

            if (vote != null) {
                if (vote.getVotedOption().equals(VoteOption.CORRECT_UNIQUE)) {
                    numberOfUnique++;
                }
                else if (vote.getVotedOption().equals(VoteOption.CORRECT_NOT_UNIQUE)) {
                    numberOfNotUnique++;
                }
                else if (vote.getVotedOption().equals(VoteOption.WRONG)) {
                    numberOfWrong++;
                }
                else {
                    numberOfNoVote++;
                }
            }
        }

        voteGetDTO.setNumberOfUnique(numberOfUnique);
        voteGetDTO.setNumberOfNotUnique(numberOfNotUnique);
        voteGetDTO.setNumberOfWrong(numberOfWrong);
        voteGetDTO.setNumberOfNoVote(numberOfNoVote);

        voteGetDTO.setPoints(calculatePoints(numberOfUnique, numberOfNotUnique, numberOfWrong));

        return voteGetDTO;
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

    private void checkIfCategoryMatches(Answer answer, String categoryName) {

        String errorMessage = "The category of voting and the answers don't match.";

        if(!answer.getCategory().getName().equals(categoryName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }

    private Answer getAnswerById(int answerId) {
        return answerRepository.findById(answerId);
    }

    private void checkIfAnswerAndVoteExists(Answer answer, Vote vote) {

        checkIfAnswerExists(answer);

        checkIfVotingAlreadyExists(vote);
    }

    private void saveVoting(Answer answer, User user, String votingString) {
        Vote newVote = new Vote();
        newVote.setAnswer(answer);
        newVote.setUser(user);
        if (answer.getAnswerString().equals("-")) {
            setVoteOption(newVote, "WRONG");
        } else {
            setVoteOption(newVote, votingString);
        }
        voteRepository.saveAndFlush(newVote);
    }

    private void setVoteOption(Vote newVote, String vote) {

        String errorMessage = "At least one of the votes is invalid!";

        try {
            if (vote == null) {
                newVote.setVotedOption(NO_VOTE);
            } else {
                newVote.setVotedOption(VoteOption.valueOf(vote));
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
}
