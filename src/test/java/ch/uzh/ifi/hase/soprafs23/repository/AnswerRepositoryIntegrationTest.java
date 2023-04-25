package ch.uzh.ifi.hase.soprafs23.repository;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class AnswerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void findByRound_success() {
        // given
        Round round = new Round();
        entityManager.persist(round);

        User user1 = new User();
        entityManager.persist(user1);

        User user2 = new User();
        entityManager.persist(user2);

        Answer answer1 = new Answer();
        answer1.setRound(round);
        answer1.setUser(user1);
        answer1.setAnswer("Answer1");
        entityManager.persist(answer1);

        Answer answer2 = new Answer();
        answer2.setRound(round);
        answer2.setUser(user2);
        answer2.setAnswer("Answer2");
        entityManager.persist(answer2);

        entityManager.flush();

        // when
        List<Answer> found = answerRepository.findByRound(round);

        // then
        assertEquals(2, found.size());
    }

    @Test
    public void findById_success() {
        // given
        Round round = new Round();
        entityManager.persist(round);

        User user = new User();
        entityManager.persist(user);

        Answer answer = new Answer();
        answer.setRound(round);
        answer.setUser(user);
        answer.setAnswer("Answer");
        entityManager.persist(answer);

        entityManager.flush();

        // when
        Answer found = answerRepository.findById(answer.getAnswerId());

        // then
        assertNotNull(found);
        assertEquals(found.getAnswerId(), answer.getAnswerId());
    }

    @Test
    public void findByRoundAndUser_success() {
        // given
        Round round = new Round();
        entityManager.persist(round);

        User user = new User();
        entityManager.persist(user);

        Answer answer = new Answer();
        answer.setRound(round);
        answer.setUser(user);
        answer.setAnswer("Answer");
        entityManager.persist(answer);

        entityManager.flush();

        // when
        List<Answer> found = answerRepository.findByRoundAndUser(round, user);

        // then
        assertEquals(1, found.size());
        assertEquals(found.get(0).getAnswerId(), answer.getAnswerId());
    }
}

