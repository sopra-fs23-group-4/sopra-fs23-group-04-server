package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.websocketDto.GameUsersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;
    private final RoundService roundService;
    GameController(GameService gameService, RoundService roundService) {
        this.roundService = roundService;
        this.gameService = gameService;

    }

    @PostMapping("/games/lobbies/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public int createGame(@RequestBody GamePostDTO gamePostDTO, @RequestHeader("Authorization") String userToken) {

        Game newGame = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        Game game = gameService.createAndReturnGame(newGame, userToken);

        return game.getGamePin();
    }

    @PostMapping("/games/{gamePin}/start")
    public void gameStart(@PathVariable("gamePin") int gamePin) {

        gameService.setUpGameForStart(gamePin);
        roundService.nextRound(gamePin);

        roundService.startRoundTime(gamePin);

    }

    @GetMapping("/games/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameCategoriesDTO getStandardCategories() {

        return gameService.getStandardCategories();
    }

    @GetMapping("/games/randomCategories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CategoryGetDTO getRandomCategory() {

        return gameService.getRandomCategory();
    }

    @GetMapping("/games/{gamePin}/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameCategoriesDTO getGameCategoriesByGamePin(@PathVariable("gamePin") int gamePin) {

        return gameService.getGameCategoriesByGamePin(gamePin);

    }

    @GetMapping("games/{gamePin}/settings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameSettingGetDTO getGameSettingByGamePin(@PathVariable("gamePin") int gamePin) {

        Game game = gameService.getGameByGamePin(gamePin);

        return UserDTOMapper.INSTANCE.convertEntityToGameSettingGetDTO(game);

    }

    @GetMapping("games/{gamePin}/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameUsersDTO getGameUsersByGamePin(@PathVariable("gamePin") int gamePin) {

        return gameService.getGameUsersByGamePin(gamePin);

    }

    @PutMapping("/games/lobbies/{gamePin}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinGame(@PathVariable("gamePin") int gamePin,
                         @RequestHeader("Authorization") String userToken) {

        gameService.joinGame(gamePin, userToken);

    }

    @PutMapping("/games/lobbies/{gamePin}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGame(@PathVariable("gamePin") int gamePin,
                          @RequestHeader("Authorization") String userToken) {

        gameService.leaveGame(gamePin, userToken);

    }

    @GetMapping("/games/lobbies/{gamePin}/winner")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<WinnerGetDTO> getWinner(@PathVariable("gamePin") int gamePin) {

        return gameService.getWinner(gamePin);

    }

    @GetMapping("/games/lobbies/{gamePin}/scoreboard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ScoreboardGetDTO> getScoreboard(@PathVariable("gamePin") int gamePin) {

        return gameService.getScoreboard(gamePin);

    }

    @GetMapping("/games/lobbies/leaderboard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LeaderboardGetDTO> getLeaderboard() {

        return gameService.getLeaderboard();

    }
}
