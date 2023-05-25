package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AnswerController {

    private final AnswerService answerService;
    Logger logger = LoggerFactory.getLogger(AnswerController.class);


    AnswerController(AnswerService answerService) {

        this.answerService = answerService;

    }

    @PostMapping("/games/{gamePin}/{roundNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void saveAnswers(@PathVariable("gamePin") int gamePin,
                            @PathVariable("roundNumber") int roundNumber,
                            @RequestHeader("Authorization") String userToken,
                            @RequestBody Map<String, String> answers) {

        answerService.saveAnswers(gamePin, userToken, roundNumber, answers);

        String logInfo = String.format(
                "Answers saved for gamePin: %d, userToken: %s, roundNumber: %d.",
                gamePin, userToken, roundNumber);
        logger.info(logInfo);
    }

    @GetMapping("/games/{gamePin}/{roundNumber}/{categoryName}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Map<Integer, String>> getAnswers(@PathVariable("gamePin") int gamePin,
                                              @PathVariable("roundNumber") int roundNumber,
                                              @PathVariable("categoryName") String categoryName,
                                              @RequestHeader("Authorization") String userToken) {

        List<Map<Integer, String>> mapList = answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);

        String logInfo = String.format(
                "Answer received from game -> gamePin: %d, roundNumber: %d, categoryName: %s.",
                gamePin, roundNumber, categoryName);
        logger.info(logInfo);

        return mapList;
    }



}
