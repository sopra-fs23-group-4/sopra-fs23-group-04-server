package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.websocketDto.RoundEndDTO;
import ch.uzh.ifi.hase.soprafs23.websocketDto.RoundTimerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RoundTimerService {
    private static final Logger logger = LoggerFactory.getLogger(RoundTimerService.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private VotingTimerService votingTimerService;

    public void startRoundTime(int gamePin) {
        logger.info("starting");
        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
        int roundLength = game.getRoundLength().getDuration();

        String logInfo = String.format("roundLength: %d.", roundLength);
        logger.info(logInfo);

        AtomicInteger remainingTime = new AtomicInteger(roundLength);

        ScheduledExecutorService roundTimer = Executors.newScheduledThreadPool(1);

        roundTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int timeLeft = remainingTime.decrementAndGet();

                if (isRoundFinished(gamePin)) {
                    String logInfo = String.format("Time stopped so standard timer stopped in game: %d.", gamePin);
                    logger.info(logInfo);
                    roundTimer.shutdownNow();
                } else if (noMoreTimeRemaining(timeLeft)) {
                    finishRoundNoTimeLeft(timeLeft, round, gamePin, roundTimer);
                } else {
                    timeLeftUpdate(timeLeft, round, gamePin);
                }
            }
        }, 0, 1, TimeUnit.SECONDS); // Schedule the task to run every 1 seconds (1000 ms)
    }

    void timeLeftUpdate(int timeLeft, Round round, int gamePin) {
        String logInfo = String.format("timeLeft: %d, current round status: %s", timeLeft, round.getStatus());
        logger.info(logInfo);

        RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
        roundTimerDTO.setTimeRemaining(timeLeft);
        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundTimerDTO);
    }

    void finishRoundNoTimeLeft(int timeLeft, Round round, int gamePin, ScheduledExecutorService roundTimer) {
        round.setStatus(RoundStatus.FINISHED);
        roundRepository.saveAndFlush(round);
        String logInfo = String.format("timeLeft: %d.", timeLeft);
        logger.info(logInfo);
        String fill = "roundEnd";
        RoundEndDTO roundEndDTO = new RoundEndDTO();
        roundEndDTO.setRounded(fill);
        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundEndDTO);
        roundTimer.shutdownNow();
        votingTimerService.startVotingTime(gamePin);
    }

    // Include the rest of your methods here...
}

