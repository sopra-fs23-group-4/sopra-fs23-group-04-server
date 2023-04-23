package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.CategoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameSettingGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.UserDTOMapper;
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

    @PostMapping("/games/lobbies/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public int createGame(@RequestBody GamePostDTO gamePostDTO, @RequestHeader("Authorization") String userToken) {

        Game newGame = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        return gameService.createGame(newGame, userToken);
    }
    @GetMapping("/game/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameCategoriesDTO getStandardCategories(){
        GameCategoriesDTO gameCategoriesDTO=new GameCategoriesDTO();
        gameCategoriesDTO.setCategories(GameCategory.getCategories());
        return gameCategoriesDTO;
    }

    @GetMapping("/game/{gameId}/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CategoryGetDTO> getGameCategoryByGameId(@PathVariable("gameId") Long gameId) {

        Game game = gameService.getGameByGameId(gameId);

        List<Category> gameCategories = game.getCategories();

        List<CategoryGetDTO> categoryGetDTOs = new ArrayList<>();

        for (Category gameCategory : gameCategories) {
            categoryGetDTOs.add(UserDTOMapper.INSTANCE.convertEntityToCategoryGetDTO(gameCategory));
        }

        return categoryGetDTOs;
    }

    @GetMapping("game/{gameId}/settings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameSettingGetDTO getGameSettingByGameId(@PathVariable("gameId") Long gameId) {

        Game game = gameService.getGameByGameId(gameId);

        return UserDTOMapper.INSTANCE.convertEntityToGameSettingGetDTO(game);
    }

    @PutMapping("/games/lobbies/{gamePin}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinGame(@PathVariable("gamePin") int gamePin, @RequestHeader("Authorization") String userToken) {

        gameService.joinGame(gamePin, userToken);

    }
}
