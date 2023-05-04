package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AnswerController {

    private final AnswerService answerService;
    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(AnswerController.class);


    AnswerController(AnswerService answerService, SimpMessagingTemplate messagingTemplate) {
        this.answerService = answerService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/games/{gamePin}/{roundNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void saveAnswers(@PathVariable("gamePin") int gamePin,
                            @PathVariable("roundNumber") int roundNumber,
                            @RequestHeader("Authorization") String userToken,
                            @RequestBody Map<String, String> answers) {

        answerService.saveAnswers(gamePin, userToken, roundNumber, answers);
    }

    @GetMapping("/games/{gamePin}/{roundNumber}/{categoryName}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Map<Long, String>> getAnswers(@PathVariable("gamePin") int gamePin,
                                              @PathVariable("roundNumber") int roundNumber,
                                              @PathVariable("categoryName") String categoryName,
                                              @RequestHeader("Authorization") String userToken) {
        log.info("Answer recieved from game : "+ gamePin +" " + roundNumber +categoryName);

        return answerService.getAnswers(gamePin, roundNumber, categoryName, userToken);
    }

}
