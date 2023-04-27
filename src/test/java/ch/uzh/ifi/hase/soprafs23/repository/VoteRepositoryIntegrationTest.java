package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class VoteRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VoteRepository voteRepository;


    @Test
    public void findByUserAndAnswer_success() {
        // given
        Round round = new Round();
        entityManager.persist(round);

        User user = new User();
        entityManager.persist(user);

        Answer answer = new Answer();
        answer.setRound(round);
        answer.setUser(user);
        answer.setAnswerString("Answer");
        entityManager.persist(answer);

        Vote vote = new Vote();
        vote.setAnswer(answer);
        vote.setUser(user);
        vote.setVotedOption(VoteOption.CORRECT_UNIQUE);
        entityManager.persist(vote);

        entityManager.flush();

        // when
        List<Vote> found = voteRepository.findByUser(user);

        // then
        assertEquals(1, found.size());
        assertEquals(found.get(0).getVoteId(), vote.getVoteId());
    }
}