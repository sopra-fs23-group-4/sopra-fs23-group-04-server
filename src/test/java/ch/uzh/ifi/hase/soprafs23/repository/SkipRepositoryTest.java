package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;

import ch.uzh.ifi.hase.soprafs23.entity.game.SkipManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

    public class SkipRepositoryTest {
        private int gamePin = 5432;

        @Test
        public void testAddGame() {
            SkipManager skipManager = SkipRepository.addGame(gamePin);
            assertNotNull(skipManager);
        }

        @Test
        public void testFindByGameId() {
            SkipManager skipManager = SkipRepository.addGame(gamePin);
            SkipManager foundSkipManager = SkipRepository.findByGameId(gamePin);
            assertEquals(skipManager, foundSkipManager);
        }

        @Test
        public void testFindByGameIdNotFound() {
            assertThrows(ResponseStatusException.class, () -> SkipRepository.findByGameId(gamePin), "This lobby does not exist!");
        }

        @Test
        public void testRemoveSkipManager() {
            SkipRepository.addGame(gamePin);
            SkipRepository.removeSkipManager(gamePin);
            assertThrows(ResponseStatusException.class, () -> SkipRepository.findByGameId(gamePin), "This lobby does not exist!");
        }
}
