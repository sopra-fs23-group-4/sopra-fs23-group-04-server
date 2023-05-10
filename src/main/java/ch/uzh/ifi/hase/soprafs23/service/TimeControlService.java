package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class TimeControlService {
    private final GameRepository gameRepository;
    private final WebSocketService webSocketService;

    private final RoundRepository roundRepository;

    public TimeControlService(@Qualifier("gameRepository") GameRepository gameRepository, WebSocketService webSocketService,
                              RoundService roundService,@Qualifier("roundRepository") RoundRepository roundRepository){
        this.gameRepository=gameRepository;
        this.webSocketService=webSocketService;
        this.roundRepository=roundRepository;
        this.

    }



    public void startRoundTime(int gamePin){
        System.out.println("starting");
        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
        assert round.getStatus()==RoundStatus.RUNNING;
        int roundLength = game.getRoundLength().getDuration();
        System.out.println(roundLength);
        AtomicInteger remainingTime = new AtomicInteger(roundLength);

        Timer roundTimer = new Timer();
        TimerTask roundTimerTask = new TimerTask() {

            @Override
            public void run() {
                int timeLeft = remainingTime.addAndGet(-1);

                if (isRoundFinished(gamePin)){
                    System.out.println("Timer canceled");
                    roundTimer.cancel();

                }
                else if (timeLeft <= 0) {
                    //finish round
                    round.setStatus(RoundStatus.FINISHED);
                    roundRepository.save(round);
                    System.out.println(timeLeft);
                    String fill="roundEnd";
                    RoundEndDTO roundEndDTO=new RoundEndDTO();
                    roundEndDTO.setRounded(fill);
                    webSocketService.sendMessageToClients(Constant.defaultDestination +gamePin, roundEndDTO);
                    roundTimer.cancel();
                    voteTimeControl(gamePin);

                }

                else{
                    //
                    System.out.println("Timeleft to answer "+ timeLeft + " current round Status " + round.getStatus());

                    RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
                    roundTimerDTO.setTimeRemaining(timeLeft);
                    webSocketService.sendMessageToClients(Constant.defaultDestination + gamePin, roundTimerDTO);
                }
            }
        };

        roundTimer.scheduleAtFixedRate(roundTimerTask,1500, 1000); // Schedule the task to run every 3 seconds (3000 ms)
    }
    public void voteTimeControl(int gamePin){
        Game game=gameRepository.findByGamePin(gamePin);
        int currentVotingRound=1;
        votingTimer(gamePin,currentVotingRound);
        System.out.println("Was here at voting");

    }


    private void resultTimer(int gamePin, int currentVotingRound) {
        System.out.println("started");
        //final int finalVotingCategory = currentVotingRound;
        Timer resultTimer = new Timer();
        TimerTask resultTimerTask = new TimerTask() {

            @Override
            public void run() {

                if (isLastCategory(gamePin,currentVotingRound)){

                    if (isFinalRound(gamePin)){
                        WebSocketDTO webSocketDTO=new WebSocketDTO();
                        webSocketDTO.setType("resultWinner");
                        webSocketService.sendMessageToClients(Constant.defaultDestination+gamePin,webSocketDTO);
                        //todo add function that ends game and makes it closed add it here
                    }
                    else{
                        WebSocketDTO webSocketDTO=new WebSocketDTO();
                        webSocketDTO.setType("resultScoreboard");
                        webSocketService.sendMessageToClients(Constant.defaultDestination+ gamePin,webSocketDTO);
                        scheduleNextRound(gamePin,4000);
                    }
                }

                else {
                    WebSocketDTO webSocketDTO=new WebSocketDTO();
                    webSocketDTO.setType("resultNextVote");
                    webSocketService.sendMessageToClients(Constant.defaultDestination+ gamePin,webSocketDTO);
                    int currentVotingRoundIncremented = currentVotingRound+1;
                    votingTimer(gamePin,currentVotingRoundIncremented);

                }
                System.out.println("Result End go to next");
            }
        };

        // Schedule votingTimerTask to run every 5 seconds
        resultTimer.schedule(resultTimerTask, 6000);
    }

    private void votingTimer(int gamePin, int currentVotingRound) {
        Timer votingTimer = new Timer();
        TimerTask votingTimerTask = new TimerTask() {
            int timeRemaining = 12; // Time remaining in seconds

            @Override
            public void run() {
                timeRemaining -= 1;

                if (timeRemaining <= 0) {
                    WebSocketDTO webSocketDTO=new WebSocketDTO();
                    webSocketDTO.setType("votingEnd");
                    webSocketService.sendMessageToClients(Constant.defaultDestination+ gamePin,webSocketDTO);
                    votingTimer.cancel();
                    System.out.println("voting End");
                    resultTimer(gamePin,currentVotingRound);
                }
                else {
                    VotingTimerDTO votingTimerDTO=new VotingTimerDTO();
                    votingTimerDTO.setTimeRemaining(timeRemaining);
                    webSocketService.sendMessageToClients(Constant.defaultDestination+ gamePin,votingTimerDTO);
                    System.out.println("Timeleft for voting: "+ timeRemaining);
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
        webSocketService.sendMessageToClients(Constant.defaultDestination+gamePin, letterDTO);
    }
//todo move time control from roundservice and voteservice to timecontrol
// todo make sure to also

}
