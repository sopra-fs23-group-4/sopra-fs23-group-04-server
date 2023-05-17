package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Constant;
import ch.uzh.ifi.hase.soprafs23.helper.WebSocketDTOCreator;
import ch.uzh.ifi.hase.soprafs23.websocketDto.VotingTimerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class VotingTimerService {

    private final WebSocketService webSocketService;
    private final ScheduledExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(VotingTimerService.class);

    private final Map<Integer, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();

    public VotingTimerService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startVotingTimer(int gamePin, int currentVotingRound) {
        int timeRemaining = 30; // Voting time in seconds
        AtomicInteger remainingTime = new AtomicInteger(timeRemaining);

        Runnable votingTimerTask = () -> {
            int timeLeft = remainingTime.decrementAndGet();
            if (timeLeft <= 0 || allPlayersWantToContinue(gamePin)) {

            }
            else {
                remainingVotingTimeUpdate(timeLeft, gamePin);
            }
        };

        // Cancel any existing timer for this game
        stopVotingTimer(gamePin);

        // Schedule the task to run every 1 second (1000 ms) with an initial delay of 2 seconds (2000 ms)
        ScheduledFuture<?> future = executorService.scheduleAtFixedRate(votingTimerTask, 2, 1, TimeUnit.SECONDS);

        // Store the future in the map so we can cancel it later
        activeTimers.put(gamePin, future);
    }

    public void stopVotingTimer(int gamePin) {
        ScheduledFuture<?> future = activeTimers.remove(gamePin);
        if (future != null) {
            future.cancel(false);
        }
    }

    private void remainingVotingTimeUpdate(int timeLeft, int gamePin) {
        VotingTimerDTO votingTimerDTO=new VotingTimerDTO();
        votingTimerDTO.setTimeRemaining(timeLeft);
        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,votingTimerDTO);
        String logInfo = String.format("Time remaining for voting: %d", timeLeft);
        logger.info(logInfo);
        // Your existing logic here...
    }

    private boolean allPlayersWantToContinue(int gamePin) {
        // Your existing logic here...
        return false; // This is a placeholder
    }

    // Any other methods you need...
}
