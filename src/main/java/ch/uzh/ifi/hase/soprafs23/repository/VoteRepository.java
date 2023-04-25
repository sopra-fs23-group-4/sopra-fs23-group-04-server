package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.entity.game.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("voteRepository")
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findByUserAndAnswer(User user, Answer answer);

    List<Vote> findByAnswer(Answer answer);
}
