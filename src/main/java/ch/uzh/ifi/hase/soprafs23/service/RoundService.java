package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.LetterDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.RoundEndDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.RoundTimerDTO;
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

@Service
@Transactional
public class RoundService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RoundRepository roundRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    private final String targetDestination="/topic/lobbies/";

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
            newRound = roundRepository.save(newRound);
            roundCounter++;
        }
        roundRepository.flush();
    }

    public LetterDTO startRound(int gamePin, int roundNumber){

        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExistsAndRuns(game);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExists(round);

        round.setStatus(RoundStatus.RUNNING);
        roundRepository.save(round);

        return nextRound(gamePin);

    }

    public void endRound(int gamePin, String userToken, int roundNumber) {
        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExistsAndRuns(game);

        User user = userRepository.findByToken(userToken);
        checkIfUserExists(user);
        checkIfUserIsInGame(game, user);

        Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
        checkIfRoundExists(round);
        checkIfRoundIsRunning(round);
        round.setStatus(RoundStatus.FINISHED);

        roundRepository.saveAndFlush(round);
    }

    public LetterDTO nextRound(int gamePin) {

        Game game = gameRepository.findByGamePin(gamePin);
        Round round = roundRepository.findByGameAndRoundNumber(game,game.getCurrentRound());

        round.setStatus(RoundStatus.RUNNING);
        roundRepository.save(round);

        LetterDTO letterDTO = new LetterDTO();
        letterDTO.setLetter(round.getLetter());
        letterDTO.setRound(round.getRoundNumber());

        game.setCurrentRound(1+game.getCurrentRound());

        return letterDTO;
    }

    public void startRoundTime(int gamePin){
        System.out.println("starting");
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

                    RoundEndDTO roundEndDTO = new RoundEndDTO();
                    webSocketService.sendMessageToClients(targetDestination + gamePin, roundEndDTO);
                    round.setStatus(RoundStatus.FINISHED);
                    roundRepository.save(round);
                    System.out.println(timeLeft);
                    timer.cancel();

                }
                else if (round.getStatus()==RoundStatus.FINISHED){
                    timer.cancel();

                }

                else{
                    System.out.println("here were we should be");

                    RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
                    roundTimerDTO.setTimeRemaining(timeLeft);
                    webSocketService.sendMessageToClients(targetDestination + gamePin, roundTimerDTO);
                }
            }
        };

        timer.scheduleAtFixedRate(updateTask, 0, 3000); // Schedule the task to run every 3 seconds (3000 ms)
    }


    /**
     * Helper methods to aid in the game creation, modification and deletion
     */

    private void checkIfGameExistsAndRuns(Game game) {

        String errorMessage = "Game does not exist or is not open anymore." +
                "Please try again with a different pin!";
        if (game == null || !game.getStatus().equals(GameStatus.RUNNING)) {
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

    private void checkIfUserExists(User user) {

        String errorMessage = "User does not exist." +
                "Please register before playing!";
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }
}
