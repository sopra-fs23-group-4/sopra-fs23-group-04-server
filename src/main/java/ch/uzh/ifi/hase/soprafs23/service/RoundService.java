package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoundService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RoundRepository roundRepository;

    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    public void createAllRounds(Game game) {
        Long roundCounter = 1L;
        List<Character> letters = game.getRoundLetters();
        for (Character letter : letters) {
            Round newRound = new Round();
            newRound.setGame(game);
            newRound.setRoundNumber(roundCounter);
            newRound.setStatus(RoundStatus.NOT_STARTED);
            newRound.setLetter(letter);
            newRound = roundRepository.save(newRound);
            roundCounter++;
        }
        roundRepository.flush();
    }
}
