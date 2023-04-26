package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("answerRepository")
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    List<Answer> findByRound(Round round);
    Answer findById(Long answerId);
    List<Answer> findByRoundAndUser(Round round, User user);

    @Query("SELECT a FROM Answer a WHERE a.round.game.id = :lobbyId")
    List<Answer> findAllByGameId(@Param("lobbyId") Long lobbyId);}
