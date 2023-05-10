package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void findByGamePin_success() {
        // given
        Game game = new Game();

        game.setHostId(1);
        game.setGamePin(1234);
        game.setRoundLength(RoundLength.SHORT);
        game.setRounds(3);
        game.setStatus(GameStatus.RUNNING);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByGamePin(game.getGamePin());

        // then
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getHostId(), game.getHostId());
        assertEquals(found.getGamePin(), game.getGamePin());
        assertEquals(found.getRoundLength(), game.getRoundLength());
        assertEquals(found.getRounds(), game.getRounds());
        assertEquals(found.getStatus(), game.getStatus());
    }
    @Test
    void findByStatus_success() {
        // given
        Game game1 = new Game();
        game1.setHostId(1);
        game1.setGamePin(1234);
        game1.setRoundLength(RoundLength.SHORT);
        game1.setRounds(3);
        game1.setStatus(GameStatus.RUNNING);

        Game game2 = new Game();
        game2.setHostId(2);
        game2.setGamePin(5678);
        game2.setRoundLength(RoundLength.LONG);
        game2.setRounds(5);
        game2.setStatus(GameStatus.RUNNING);

        entityManager.persist(game1);
        entityManager.persist(game2);
        entityManager.flush();

        // when
        List<Game> found = gameRepository.findByStatus(GameStatus.RUNNING);

        // then
        assertEquals(2, found.size());
        assertEquals(GameStatus.RUNNING, found.get(0).getStatus());
        assertEquals(GameStatus.RUNNING, found.get(1).getStatus());
    }

    @Test
    void findByGameId_success() {
        // given
        Game game = new Game();
        game.setHostId(1);
        game.setGamePin(1234);
        game.setRoundLength(RoundLength.SHORT);
        game.setRounds(3);
        game.setStatus(GameStatus.RUNNING);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByGameId(game.getGameId());

        // then
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getHostId(), game.getHostId());
        assertEquals(found.getGamePin(), game.getGamePin());
        assertEquals(found.getRoundLength(), game.getRoundLength());
        assertEquals(found.getRounds(), game.getRounds());
        assertEquals(found.getStatus(), game.getStatus());
    }
    @Test
    void findByStatus_notFound() {
        // given
        Game game1 = new Game();
        game1.setHostId(1);
        game1.setGamePin(1234);
        game1.setRoundLength(RoundLength.SHORT);
        game1.setRounds(3);
        game1.setStatus(GameStatus.RUNNING);

        entityManager.persist(game1);
        entityManager.flush();

        // when
        List<Game> found = gameRepository.findByStatus(GameStatus.OPEN);

        // then
        assertTrue(found.isEmpty());
    }

    @Test
    void findByGameId_notFound() {
        // given
        Game game = new Game();
        game.setHostId(1);
        game.setGamePin(1234);
        game.setRoundLength(RoundLength.SHORT);
        game.setRounds(3);
        game.setStatus(GameStatus.CLOSED);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByGameId(-1);

        // then
        assertNull(found);
    }
}