package ch.uzh.ifi.hase.soprafs23.entity.game;

import ch.uzh.ifi.hase.soprafs23.constant.Skip;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

public class SkipManager {
    private static HashMap<User, Skip> userContinueMap = new HashMap<>();

    public void cleanUp(List<User> players){
        userContinueMap.clear();
        for (User player: players){
            userContinueMap.put(player,Skip.DoesNotWantToSkip);
        }
    }

    public void addPlayersForFirstRound(List<User> players) {
        for (User player: players){
            userContinueMap.put(player,Skip.DoesNotWantToSkip);
        }
    }
    public  boolean allPlayersWantToContinue(){
        for (Skip skip : userContinueMap.values()) {
            if (skip == Skip.DoesNotWantToSkip) {
                return false;
            }
        }
        return true;
    }

    public void userWantsToSkip(User user){
        Skip skip= userContinueMap.get(user);
        if (skip==Skip.WantsToSkip){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You already want to skip");
        }
        userContinueMap.put(user,Skip.WantsToSkip);
    }

}
