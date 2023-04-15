package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.OPEN;
import static ch.uzh.ifi.hase.soprafs23.constant.GameStatus.RUNNING;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final GameRepository gameRepository;

    private final GameUserRepository gameUserRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("gameUserRepository") GameUserRepository gameUserRepository) {
        this.gameRepository = gameRepository;
        this.gameUserRepository = gameUserRepository;
    }

    public List<Game> getOpenGames() {
        return gameRepository.findGames(OPEN);
    }

    public List<Game> getRunningGames() {
        return gameRepository.findGames(RUNNING);
    }

    public List<Game> getOpenOrRunningGames() {
        List<Game> openOrRunningGames = getOpenGames();
        for (Game game : getRunningGames()) {
            openOrRunningGames.add(game);
        }
        return openOrRunningGames;
    }

    public List<Long> getGameUsers(Long gameId) {
        return gameUserRepository.findByGameId(gameId);
    }

    public Long createGame(Game newGame) {

        checkIfHostIsEligible(newGame);

        // TODO create and set gamePin
        // newGame.setGamePin();
        newGame.setStatus(OPEN);


        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created following game: {}", newGame);
        return newGame.getGamePin();
    }

    /**
     * Helper methods to aid in the game creation, modification and deletion
     */

    private void checkIfHostIsEligible(Game gameToBeCreated) {
        Long hostId = gameToBeCreated.getHostId();
        List<Game> openOrRunningGames = getOpenOrRunningGames();

        String errorMessage = "You are already part of a game." +
                "You cannot host another game!";
        for (Game game : openOrRunningGames) {
            List<Long> userIds = getGameUsers(game.getId());
            if (hostId.equals(game.getHostId()) || userIds.contains(hostId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format(errorMessage));
            }
        }
    }
}
