package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.webSockets.DTO.ScoreboardEntryDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class WebsocketService {

    private AnswerRepository answerRepository;
    protected SimpMessagingTemplate smesg;

    public void sendMessageToClients(String destination, Object dto) {
        this.smesg.convertAndSend(destination, dto);
    }

    private Map<User, Integer> calculateUserScores(Long lobbyId) {
        List<Answer> answers = answerRepository.findAllByGameId(lobbyId);

        Map<User, Integer> userScores = new HashMap<>();
        for (Answer answer : answers) {
            User user = answer.getUser();
            int currentScore = userScores.getOrDefault(user, 0);
            currentScore += answer.getScorePoint().getPoints();
            userScores.put(user, currentScore);
        }

        return userScores;
    }

    public Map.Entry<List<User>, Integer> getWinner(Long lobbyId) {
        Map<User, Integer> userScores = calculateUserScores(lobbyId);

        List<User> winners = new ArrayList<>();
        int maxScore = -1;
        for (Map.Entry<User, Integer> entry : userScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                winners.clear();
                winners.add(entry.getKey());
                maxScore = entry.getValue();
            } else if (entry.getValue() == maxScore) {
                winners.add(entry.getKey());
            }
        }

        return new AbstractMap.SimpleEntry<>(winners, maxScore);
    }

    public List<ScoreboardEntryDTO> getScoreboard(Long lobbyId) {
        Map<User, Integer> userScores = calculateUserScores(lobbyId);

        List<ScoreboardEntryDTO> scoreboard = new ArrayList<>();
        for (Map.Entry<User, Integer> entry : userScores.entrySet()) {
            ScoreboardEntryDTO scoreboardEntry = new ScoreboardEntryDTO();
            scoreboardEntry.setUser(entry.getKey());
            scoreboardEntry.setScore(entry.getValue());
            scoreboard.add(scoreboardEntry);
        }

        return scoreboard;
    }
}

