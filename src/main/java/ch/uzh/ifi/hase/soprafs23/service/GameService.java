package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GameService {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private int lobbyKey;


    Logger log = LoggerFactory.getLogger(GameService.class);


    public GameService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository")PlayerRepository playerRepository){
        this.userRepository=userRepository;
        this.playerRepository=playerRepository;
        this.lobbyKey =0;
    }

    private static void checkIfLobbyForPlayerExists(Player playerToBeCreated) {
        try {
            GameRepository.findByLobbyId((int) playerToBeCreated.getLobbyId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Adding the player failed: " + e.getMessage());

        }

    }
    public int createLobby(Player playerToHost){

        checkIfPlayerAuthorized(playerToHost);
        GameRepository.checkIfPlayerAlreadyInGame(playerToHost);
        lobbyKey++;
        Game newGame=new Game();
        newGame.setHostToken(playerToHost.getToken());
        GameRepository.addGame(lobbyKey,newGame);
        playerToHost.setLobbyId(lobbyKey);
        playerRepository.saveAndFlush(playerToHost);


        return lobbyKey;
    }

    private void checkIfPlayerAuthorized(Player player){
        User foundUser=userRepository.findByToken(player.getToken());
        if (foundUser==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized");
        }
    }
    public void addPlayer(Player playerToJoin){
        checkIfPlayerAuthorized(playerToJoin);
        checkIfLobbyForPlayerExists(playerToJoin);
        GameRepository.checkIfPlayerAlreadyInGame(playerToJoin);

        playerRepository.saveAndFlush(playerToJoin);


    }




}
