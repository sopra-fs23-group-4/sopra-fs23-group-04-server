package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyIdDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

@RestController
public class LobbyController {
    private final GameService gameService;

    Logger log= LoggerFactory.getLogger(LobbyController.class);

    public LobbyController(GameService gameService){
        this.gameService=gameService;
    }

    @PostMapping("/game/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyIdDTO createLobby(@RequestBody PlayerDTO playerDTO){
        System.out.println(playerDTO.getToken());
        LobbyIdDTO lobbyIdDTO=new LobbyIdDTO();
        Player host= DTOMapper.INSTANCE.convertPlayerDTOToEntity(playerDTO);
        lobbyIdDTO.setLobbyId(gameService.createLobby(host));
        return lobbyIdDTO;
    }
}
