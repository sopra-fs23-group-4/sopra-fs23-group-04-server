package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.RoundService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.GameUsersDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.LetterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameController {

    public static final String FinalDestination = "/topic/lobbies/";
    private final GameService gameService;
    private final WebSocketService webSocketService;
    private final RoundService roundService;

    GameController(GameService gameService,
                   WebSocketService webSocketService,
                    RoundService roundService) {

        this.gameService = gameService;
        this.webSocketService=webSocketService;
        this.roundService=roundService;

    }

    @PostMapping("/games/lobbies/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public int createGame(@RequestBody GamePostDTO gamePostDTO, @RequestHeader("Authorization") String userToken) {

        Game newGame = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        return gameService.createGame(newGame, userToken);
    }

    @GetMapping("/games/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameCategoriesDTO getStandardCategories(){
        GameCategoriesDTO gameCategoriesDTO=new GameCategoriesDTO();
        gameCategoriesDTO.setCategories(GameCategory.getCategories());
        return gameCategoriesDTO;
    }


    @PostMapping("/games/{gamePin}/start")
    public void gameStart(@PathVariable("gamePin") int gamePin) {
        LetterDTO letterDTO= gameService.startGame(gamePin);
        webSocketService.sendMessageToClients(FinalDestination +gamePin, letterDTO);
        roundService.startRoundTime(gamePin);
    }

    @GetMapping("/games/{gamePin}/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameCategoriesDTO getGameCategoryByGamePin(@PathVariable("gamePin") int gamePin) {

        Game game = gameService.getGameByGamePin(gamePin);

        List<String> gameCategoryNames = gameService.getGameCategoryNames(game);

        GameCategoriesDTO gameCategoriesDTO = new GameCategoriesDTO();
        gameCategoriesDTO.setCategories(gameCategoryNames);

        return gameCategoriesDTO;
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

        Game game = gameService.getGameByGamePin(gamePin);

        return gameService.getHostAndAllUserNamesOfGame(game);
    }

    @PutMapping("/games/lobbies/{gamePin}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinGame(@PathVariable("gamePin") int gamePin,
                         @RequestHeader("Authorization") String userToken) {

        GameUsersDTO gameUsersDTO = gameService.joinGame(gamePin, userToken);

        webSocketService.sendMessageToClients(FinalDestination + gamePin, gameUsersDTO);
    }



    @PutMapping("/games/lobbies/{gamePin}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGame(@PathVariable("gamePin") int gamePin,
                          @RequestHeader("Authorization") String userToken) {

        GameUsersDTO gameUsersDTO = gameService.leaveGame(gamePin, userToken);

        try {
            gameService.checkIfGameExists(gameService.getGameByGamePin(gamePin));
            webSocketService.sendMessageToClients(FinalDestination + gamePin, gameUsersDTO);
        }
        catch (ResponseStatusException ignored) {}

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

    @GetMapping("/games/lobbies/Leaderboard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LeaderboardGetDTO> getLeaderboard(){
        return gameService.getLeaderboard();
    }
}
