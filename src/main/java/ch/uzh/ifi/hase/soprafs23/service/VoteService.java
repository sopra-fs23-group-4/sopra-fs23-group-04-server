package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.*;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.VoteOptionsGetDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class VoteService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;
    private final CategoryRepository categoryRepository;
    private final RoundRepository roundRepository;
    private final WebSocketService webSocketService;

    private final String targetDestination="/topic/lobbies/";


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
        this.roundRepository = roundRepository;
        this.webSocketService=webSocketService;
    }

    /*public void votingTimer(int gamePin){
        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
        round.setStatus(RoundStatus.RUNNING);
        int roundLength = game.getRoundLength().getDuration();
        System.out.println(roundLength);
        AtomicInteger remainingTime = new AtomicInteger(roundLength);

        Timer timer = new Timer();
        TimerTask updateTask = new TimerTask() {

            @Override
            public void run() {
                System.out.println("was here");
                int timeLeft = remainingTime.addAndGet(-3);
                if (timeLeft <= 0) {


                    String type="endVote";
                    webSocketService.sendMessageToClients(targetDestination+gamePin, type);
                    round.setStatus(RoundStatus.FINISHED);
                    roundRepository.save(round);
                    System.out.println(timeLeft);
                    timer.cancel();

                }

                else{
                    System.out.println(timeLeft);

                    RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
                    roundTimerDTO.setTimeRemaining(timeLeft);
                    webSocketService.sendMessageToClients(targetDestination + gamePin, roundTimerDTO);
                }
            }
        };

        timer.scheduleAtFixedRate(updateTask, 0, 3000);
    }*/


    public void voteTimer(int gamePin){
        Game game=gameRepository.findByGamePin(gamePin);
        int numberOfVotingRounds=game.getCategories().size();
        int currentVotingRound=0;

        while (currentVotingRound< numberOfVotingRounds) {
            Timer votingTimer = new Timer();
            TimerTask votingTimerTask = new TimerTask() {
                int timeRemaining = 12; // Time remaining in seconds

                @Override
                public void run() {
                    timeRemaining -= 3;
                    VotingTimerDTO votingTimerDTO=new VotingTimerDTO();
                    votingTimerDTO.setTimeRemaining(timeRemaining);
                    webSocketService.sendMessageToClients(targetDestination+gamePin,votingTimerDTO);

                    if (timeRemaining <= 0) {
                        VotingEndDTO votingEndDTO=new VotingEndDTO();
                        webSocketService.sendMessageToClients(targetDestination+gamePin,votingEndDTO);
                        votingTimer.cancel();
                    }
                }
            };
            votingTimer.schedule(votingTimerTask, 0, 3000);



            final int finalCurrentVotingRound = currentVotingRound;
            Timer showResults = new Timer();
            TimerTask showResultsTask = new TimerTask() {
                int timeRemaining = 5;



                @Override
                public void run() {


                    timeRemaining -= 5;
                    if (timeRemaining <= 0){
                        if (finalCurrentVotingRound == numberOfVotingRounds - 1){
                            ShowScoreBoardDTO showScoreBoardDTO=new ShowScoreBoardDTO();
                            webSocketService.sendMessageToClients(targetDestination+gamePin,showScoreBoardDTO);

                        }
                        else {
                            NextVotingDTO nextVotingDTO= new NextVotingDTO();
                            webSocketService.sendMessageToClients(targetDestination+gamePin,nextVotingDTO);
                        }

                    }

                    System.out.println("bye");


                }
            };

            // Schedule votingTimerTask to run every 5 seconds
            showResults.schedule(showResultsTask, 2000, 5000);
            currentVotingRound+=1;
        }


    }
    public void saveVote(int gamePin, String categoryName, String userToken, Map<Long, String> votings) {

        Game game = gameRepository.findByGamePin(gamePin);
        User user = userRepository.findByToken(userToken);

        checkIfGameExists(game);
        checkIfUserExists(user);

        checkIfUserIsInGame(game, user);

        for (Map.Entry<Long, String> voting : votings.entrySet()) {

            Long answerId = voting.getKey();
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
        User user = userRepository.findByToken(userToken);

        checkIfGameExists(game);
        checkIfRoundExists(round);
        checkIfCategoryExists(category);
        checkIfUserExists(user);
        checkIfUserIsInGame(game, user);

        List<User> users = game.getUsers();

        return getVoteGetDTOList(game, round, category, users);

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

    private void checkIfRoundExists(Round round) {

        String errorMessage = "Round does not exist.";

        if (round == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
    private void checkIfCategoryExists(Category category) {

        String errorMessage = "Category does not exist.";

        if (category == null) {
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

    private List<VoteGetDTO> getVoteGetDTOList(Game game, Round round, Category category, List<User> users) {

        List<VoteGetDTO> voteGetDTOList = new ArrayList<>();

        for (User user : users) {

            Answer answer = answerRepository.findByRoundAndCategoryAndUser(round, category, user);
            checkIfAnswerExists(answer);

            List<User> allUsersFiltered = new ArrayList<>(users);
            allUsersFiltered.remove(user);

            VoteGetDTO newVoteGetDTO = createVoteGetDTO(user.getUsername(), allUsersFiltered,answer);
            voteGetDTOList.add(newVoteGetDTO);
        }

        return voteGetDTOList;

    }

    private VoteGetDTO createVoteGetDTO(String username, List<User> allUsersFiltered, Answer answer) {
        int numberOfUnique = 0;
        int numberOfNotUnique = 0;
        int numberOfWrong = 0;

        VoteGetDTO voteGetDTO = new VoteGetDTO();
        voteGetDTO.setUsername(username);
        voteGetDTO.setAnswerString(answer.getAnswerString());



        for (User user : allUsersFiltered) {

            Vote vote = voteRepository.findByUserAndAnswer(user, answer);

            if (vote.getVotedOption().equals(VoteOption.CORRECT_UNIQUE)) {
                numberOfUnique++;
            }
            else if (vote.getVotedOption().equals(VoteOption.CORRECT_NOT_UNIQUE)) {
                numberOfNotUnique++;
            }
            else {
                numberOfWrong++;
            }
        }

        voteGetDTO.setNumberOfUnique(numberOfUnique);
        voteGetDTO.setNumberOfNotUnique(numberOfNotUnique);
        voteGetDTO.setNumberOfWrong(numberOfWrong);

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
    private Answer getAnswerById(Long answerId) {
        return answerRepository.findById(answerId);
    }

    private void checkIfAnswerAndVoteExists(Answer answer, Vote vote) {

        checkIfAnswerExists(answer);

        checkIfVoteExists(vote);
    }

    private void checkIfAnswerExists(Answer answer) {

        String errorMessage = "This answer does not exist.";

        if (answer == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfVoteExists(Vote vote) {

        String errorMessage = "This Voting has already been saved.";

        if (vote != null) {
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
