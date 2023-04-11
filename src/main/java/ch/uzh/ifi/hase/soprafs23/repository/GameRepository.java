package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

public class GameRepository {
    private static final HashMap<Integer, Game> gameRepo = new HashMap<>();

    private GameRepository() {
    }

    public static void addGame(int lobbyId, Game game) {
        gameRepo.put(lobbyId, game);
    }

    public static void removeGame(int lobbyId) {
        gameRepo.remove(lobbyId);
    }

    public static Game findByLobbyId(int lobbyId) {
        Game game = gameRepo.get(lobbyId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This lobby does not exist!");
        }
        return game;
    }

    public static void checkIfPlayerAlreadyInGame(Player player) {
        String playerToken=player.getToken();
        for (Game game: gameRepo.values()){
            if (game.inGame(playerToken)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already in a game");
            }
        }
    }
}
