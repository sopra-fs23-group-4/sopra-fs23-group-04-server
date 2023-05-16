package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerHelper {

    private AnswerHelper() {}

    public static void checkIfAnswerExists(Answer answer) {

        String errorMessage = "This answer does not exist.";

        if (answer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
    }
    public static List<Map<Integer, String>> filterAnswersByDeletingUser(List<Answer> answers, User user) {

        List<Map<Integer, String>> filteredAnswers = new ArrayList<>();

        for (Answer answer : answers) {
            if (!answer.getUser().equals(user)) {
                Map<Integer, String> answerTuple = new HashMap<>();
                answerTuple.put(answer.getAnswerId(), answer.getAnswerString());
                filteredAnswers.add(answerTuple);
            }
        }
        return filteredAnswers;
    }
}
