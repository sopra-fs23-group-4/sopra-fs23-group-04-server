package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.websocketDto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.uzh.ifi.hase.soprafs23.constant.RoundStatus.NOT_STARTED;
import static ch.uzh.ifi.hase.soprafs23.helper.GameHelper.*;
import static ch.uzh.ifi.hase.soprafs23.helper.UserHelper.*;

@Service
@Transactional
public class RoundService {

    private final RoundRepository roundRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final Logger logger = LoggerFactory.getLogger(RoundService.class);



    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository,
                        @Qualifier("gameRepository")GameRepository gameRepository,
                        @Qualifier("userRepository") UserRepository userRepository,
                        WebSocketService webSocketService) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.webSocketService=webSocketService;
    }

    public void createAllRounds(Game game) {
        int roundCounter = 1;
        List<Character> letters = game.getRoundLetters();
        for (Character letter : letters) {
            Round newRound = new Round();
            newRound.setGame(game);
            newRound.setRoundNumber(roundCounter);
            newRound.setStatus(NOT_STARTED);
            newRound.setLetter(letter);
            roundRepository.save(newRound);
            roundCounter++;
        }
        roundRepository.flush();
    }


    //stopRound
    public void stopRound(int gamePin, String userToken, int roundNumber) {

        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExists(game);
        checkIfGameIsRunning(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);
        checkIfUserIsInGame(game, user);


        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExists(round);
        checkIfRoundIsRunning(round);
        round.setStatus(RoundStatus.FINISHED);

        roundRepository.saveAndFlush(round);
        voteTimeControl(gamePin);

    }

    public void nextRound(int gamePin) {

        Game game = gameRepository.findByGamePin(gamePin);
        int currentRound= game.incrementRound();
        Round round = roundRepository.findByGameAndRoundNumber(game,currentRound);

        round.setStatus(RoundStatus.RUNNING);
        roundRepository.save(round);
        gameRepository.saveAndFlush(game);

        LetterDTO letterDTO = new LetterDTO();
        letterDTO.setLetter(round.getLetter());
        letterDTO.setRound(round.getRoundNumber());
        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION +gamePin, letterDTO);
    }


    private void checkIfRoundExists(Round round) {
        String errorMessage = "Round does not exist.";

        if (round == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    private void checkIfRoundIsRunning(Round round) {
        String errorMessage = "Round is not running anymore. Not possible to save your answers!";

        if (!round.getStatus().equals(RoundStatus.RUNNING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }


    public void startRoundTime(int gamePin){
        logger.info("starting");
        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
        assert round.getStatus()==RoundStatus.RUNNING;
        int roundLength = game.getRoundLength().getDuration();

        String logInfo = String.format("roundLength: %d.", roundLength);
        logger.info(logInfo);

        AtomicInteger remainingTime = new AtomicInteger(roundLength);

        Timer roundTimer = new Timer();
        TimerTask roundTimerTask = new TimerTask() {

            @Override
            public void run() {
                int timeLeft = remainingTime.addAndGet(-1);

                if (isRoundFinished(gamePin)){
                    String logInfo = String.format("Time stopped so standard timer stopped in game: %d.", gamePin);
                    logger.info(logInfo);
                    roundTimer.cancel();

                }
                else if (noMoreTimeRemaining(timeLeft)) {
                    //finish round
                    round.setStatus(RoundStatus.FINISHED);
                    roundRepository.save(round);
                    String logInfo = String.format("timeLeft: %d.", timeLeft);
                    logger.info(logInfo);
                    String fill="roundEnd";
                    RoundEndDTO roundEndDTO = new RoundEndDTO();
                    roundEndDTO.setRounded(fill);
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundEndDTO);
                    roundTimer.cancel();
                    voteTimeControl(gamePin);

                }

                else {
                    //
                    String logInfo = String.format("timeLeft: %d, current round status: %s", timeLeft, round.getStatus());
                    logger.info(logInfo);

                    RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
                    roundTimerDTO.setTimeRemaining(timeLeft);
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundTimerDTO);
                }
            }
        };

        roundTimer.scheduleAtFixedRate(roundTimerTask,1500, 1000); // Schedule the task to run every 3 seconds (3000 ms)
    }
    public void voteTimeControl(int gamePin){

        String logInfo = String.format("Voting starting for game: %d.", gamePin);
        logger.info(logInfo);

        gameRepository.findByGamePin(gamePin);
        int currentVotingRound=1;
        votingTimer(gamePin,currentVotingRound);

    }


    private void votingScoreOverviewTimer(int gamePin, int currentVotingRound) {
        logger.info("started");

        Timer resultTimer = new Timer();

        AtomicInteger remainingTime = new AtomicInteger(8);
        TimerTask resultTimerTask = new TimerTask() {

            @Override
            public void run() {
                int timeLeft = remainingTime.addAndGet(-1);
                //todo @Vale one needs add an iff statement that when all players want to continue one does something (oMoreTimeRemaining or all players want to continue)
                if (noMoreTimeRemaining(timeLeft) ) {
                    resultTimer.cancel();
                    if (isLastCategory(gamePin,currentVotingRound)){

                        if (isFinalRound(gamePin)){
                            WebSocketDTO webSocketDTO=new WebSocketDTO();
                            webSocketDTO.setType("resultWinner");
                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION +gamePin,webSocketDTO);
                            endGame(gamePin);
                        }
                        else{
                            WebSocketDTO webSocketDTO=new WebSocketDTO();
                            webSocketDTO.setType("resultScoreboard");
                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                            scheduleNextRound(gamePin,4000);
                        }
                    }

                    else {
                        WebSocketDTO webSocketDTO=new WebSocketDTO();
                        webSocketDTO.setType("resultNextVote");
                        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                        int currentVotingRoundIncremented = currentVotingRound+1;
                        votingTimer(gamePin,currentVotingRoundIncremented);

                    }
                }
            }
        };

        // Schedule votingTimerTask to run every 5 seconds
        resultTimer.schedule(resultTimerTask, 1500,1000);
    }

    private static boolean noMoreTimeRemaining(int timeRemaining){
        return timeRemaining<=0;
    }

    private void votingTimer(int gamePin, int currentVotingRound) {
        Timer votingTimer = new Timer();
        TimerTask votingTimerTask = new TimerTask() {
            int timeRemaining = 12; // Time remaining in seconds

            @Override
            public void run() {
                timeRemaining -= 1;
//todo @vale you want to see the scoreboard and or statement which assures that all players want to continue then do execute the if statement below
                //something like if (timeRemaing <= 0 || allPlayerswantToContinue=true)
                if (noMoreTimeRemaining(timeRemaining)) {
                    votingTimer.cancel();
                    WebSocketDTO webSocketDTO=new WebSocketDTO();
                    webSocketDTO.setType("votingEnd");
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                    logger.info("Voting ended, the users see voting results now.");
                    votingScoreOverviewTimer(gamePin,currentVotingRound);
                }
                else {
                    VotingTimerDTO votingTimerDTO=new VotingTimerDTO();
                    votingTimerDTO.setTimeRemaining(timeRemaining);
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,votingTimerDTO);
                    String logInfo = String.format("Time remaining for voting: %d", timeRemaining);
                    logger.info(logInfo);
                }

            }
        };
        votingTimer.schedule(votingTimerTask, 2000, 1000);
    }
    private void scheduleNextRound(int gamePin, int delay) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                nextRound(gamePin);
                startRoundTime(gamePin);
            }
        };

        // Schedule the task to run after the specified delay
        timer.schedule(task, delay);
    }

    private boolean isFinalRound(int gamePin) {
        Game game= gameRepository.findByGamePin(gamePin);
        return game.getRounds()==game.getCurrentRound();
    }
    private boolean isLastCategory(int gamePin, int currentVotingRound) {
        Game game=gameRepository.findByGamePin(gamePin);
        return currentVotingRound==game.getNumberOfCategories();
    }
    private boolean isRoundFinished(int gamePin){
        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
        return round.getStatus()==RoundStatus.FINISHED;
    }

    private void endGame(int gamePin){
        Game game=gameRepository.findByGamePin(gamePin);
        game.setStatus(GameStatus.CLOSED);
        gameRepository.saveAndFlush(game);
    }

}
