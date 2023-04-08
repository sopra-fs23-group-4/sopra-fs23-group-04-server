package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
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
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    Logger log = LoggerFactory.getLogger(GameService.class);

    public GameService(@Qualifier("userRepository") UserRepository userRepository,@Qualifier("gameRepository") GameRepository gameRepository){
        this.userRepository=userRepository;
        this.gameRepository=gameRepository;
    }

    public int createLobby(User host){
        checkIfUserExists(host);
        return 1;
    }

    private void checkIfUserExists(User user){
        if (userRepository.findByToken(user.getToken()) ==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }


}
