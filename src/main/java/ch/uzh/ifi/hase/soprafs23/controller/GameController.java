package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameCategory;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameSettingGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.GameCategoriesDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.GameUsersDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.LetterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate=messagingTemplate;
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

    @PostMapping("/game/{gamePin}/start-game")
    public void gameStart(@PathVariable("gamePin") int gamePin) {
        LetterDTO letterDTO= gameService.startGame(gamePin);
        messagingTemplate.convertAndSend("/topic/lobbies/" +gamePin, letterDTO);
    }

    @GetMapping("/game/{gamePin}/categories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameCategoriesDTO getGameCategoryByGamePin(@PathVariable("gamePin") int gamePin) {

        Game game = gameService.getGameByGamePin(gamePin);

        List<String> gameCategoryNames = gameService.getGameCategoryNames(game);

        GameCategoriesDTO gameCategoriesDTO=new GameCategoriesDTO();
        gameCategoriesDTO.setCategories(gameCategoryNames);

        return gameCategoriesDTO;
    }

    @GetMapping("game/{gamePin}/settings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameSettingGetDTO getGameSettingByGamePin(@PathVariable("gamePin") int gamePin) {

        Game game = gameService.getGameByGamePin(gamePin);

        return UserDTOMapper.INSTANCE.convertEntityToGameSettingGetDTO(game);
    }

    @GetMapping("game/{gamePin}/users")
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

        messagingTemplate.convertAndSend("/topic/lobbies/" + gamePin, gameUsersDTO);
    }

    @SendTo("/topic/lobbies/{gamePin}")
    public Object sendMsg(@Payload Object objectDTO,
                          @DestinationVariable String gamePin) {
        return objectDTO;
    }


    @PutMapping("/games/lobbies/{gamePin}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGame(@PathVariable("gamePin") int gamePin,
                          @RequestHeader("Authorization") String userToken) {

        GameUsersDTO gameUsersDTO = gameService.leaveGame(gamePin, userToken);

        try {
            gameService.checkIfGameExists(gameService.getGameByGamePin(gamePin));
            messagingTemplate.convertAndSend("/topic/lobbies/" + gamePin, gameUsersDTO);
        }
        catch (ResponseStatusException ignored) {}

    }
}
