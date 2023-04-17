package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.CategoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game/lobby/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public int createGame(@RequestBody GamePostDTO gamePostDTO) {

        Game newGame = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        int gamePin = gameService.createGame(newGame);

        return gamePin;
    }

    @GetMapping("/game/{gameId}/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CategoryGetDTO> getGameCategoryByGameId(@PathVariable("gameId") Long gameId) {

        Game game = gameService.getGameByGameId(gameId);

        List<Category> gameCategories = game.getCategories();

        List<CategoryGetDTO> categoryGetDTOs = new ArrayList<>();

        for (Category gameCategory : gameCategories) {
            categoryGetDTOs.add(DTOMapper.INSTANCE.convertEntityToCategoryGetDTO(gameCategory));
        }

        return categoryGetDTOs;
    }
}
