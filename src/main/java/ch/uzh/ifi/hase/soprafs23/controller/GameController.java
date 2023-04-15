package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.GameCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class GameController {

    private final GameService gameService;
    private final GameCategoryService gameUserService;

    GameController(GameService gameService, GameCategoryService gameUserService) {
        this.gameService = gameService;
        this.gameUserService = gameUserService;
    }

    @PostMapping("/game/lobby/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Long createGame(@RequestBody GamePostDTO gamePostDTO) {

        Game newGame = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        Long gamePin = gameService.createGame(newGame);
        gameUserService.saveCategories(newGame.getId(), gamePostDTO.getCategories());

        return gamePin;
    }
}
