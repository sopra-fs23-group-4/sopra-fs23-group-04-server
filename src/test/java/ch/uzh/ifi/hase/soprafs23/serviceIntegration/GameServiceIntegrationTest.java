package ch.uzh.ifi.hase.soprafs23.serviceIntegration;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GameServiceIntegrationTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;

    private User user1;
    private User user2;
    private User user3;
    private Game game;

    @BeforeEach
    void setUp() {
        User userForCreation = new User();
        userForCreation.setUsername("user1");
        userForCreation.setPassword("testPassword");
        user1 = userService.createAndReturnUser(userForCreation);

        User userForCreation2 = new User();
        userForCreation2.setUsername("user2");
        userForCreation2.setPassword("testPassword");
        user2 = userService.createAndReturnUser(userForCreation2);

        User userForCreation3 = new User();
        userForCreation3.setUsername("user3");
        userForCreation3.setPassword("testPassword");
        user3 = userService.createAndReturnUser(userForCreation3);

        Game gameForCreation = new Game();
        gameForCreation.setRounds(10);
        gameForCreation.setRoundLength(RoundLength.MEDIUM);
        gameForCreation.setCategories(getCategories());
        game = gameForCreation;

    }

    @Test
    void createAndReturnGame_validInput_gameCreated() {

        String user1Token = user1.getToken();

        AtomicReference<Game> createdGame = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            createdGame.set(gameService.createAndReturnGame(game, user1Token));
        });

        assertNotNull(createdGame.get());

    }

    private List<Category> getCategories() {

        List<String> categoryNames = Arrays.asList("Stadt", "Land", "Auto", "Film Regisseur");

        List<Category> mappedCategories = new ArrayList<>();

        for(String categoryName : categoryNames) {
            Category mappedCategory = new Category();
            mappedCategory.setName(categoryName);
            mappedCategories.add(mappedCategory);
        }
        return mappedCategories;
    }
}
