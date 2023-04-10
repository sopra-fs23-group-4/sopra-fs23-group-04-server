package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.GameParticipant;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameParticipantRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameParticipantRepository gameParticipantRepository;
    Logger log = LoggerFactory.getLogger(GameService.class);

    public GameService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("gameParticipantRepository") GameParticipantRepository gameParticipantRepository){
        this.userRepository=userRepository;
        this.gameRepository=gameRepository;
        this.gameParticipantRepository = gameParticipantRepository;
    }


    private void checkIfUserExists(User user){
        if (userRepository.findByToken(user.getToken()) ==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
    private void checkIfUserAlreadyInActiveGame(User user){
        List<Game> playedGames=gameParticipantRepository.findByUser(user);
        for (Game game: playedGames){
            if (game.getGameState()!= GameState.FINISHED){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Dear user you are already in a game");
            }
        }
    }

    private String createGame(User hostToken){
        User host=userRepository.findByToken(hostToken.getToken());
        checkIfUserExists(host);
        checkIfUserAlreadyInActiveGame(host);

        Game newGame= new Game();
        newGame.setGameState(GameState.INGAME);
        gameRepository.saveAndFlush(newGame);

        GameParticipant newHostParticipant= new GameParticipant();
        newHostParticipant.setHost(true);
        newHostParticipant.setGame(newGame);
        newHostParticipant.setUser(host);
        gameParticipantRepository.saveAndFlush(newHostParticipant);

        return newGame.getPin();
    }




}
