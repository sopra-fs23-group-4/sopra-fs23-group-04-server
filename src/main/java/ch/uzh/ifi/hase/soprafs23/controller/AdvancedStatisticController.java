package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.rest.dto.game.AdvancedStatisticGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.AdvancedStatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdvancedStatisticController {

    private final AdvancedStatisticService advancedStatisticService;

    Logger log = LoggerFactory.getLogger(AdvancedStatisticController.class);

    public AdvancedStatisticController(AdvancedStatisticService advancedStatisticService) {
        this.advancedStatisticService = advancedStatisticService;
    }

    @GetMapping("users/{userId}/advancedStatistics")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AdvancedStatisticGetDTO getAdvancedUserStatistic (@PathVariable ("userId") int userId){

        return advancedStatisticService.getAdvancedUserStatistic(userId);
    }

}
