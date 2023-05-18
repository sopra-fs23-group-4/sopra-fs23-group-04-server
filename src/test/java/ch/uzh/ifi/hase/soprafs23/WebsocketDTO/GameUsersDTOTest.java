package ch.uzh.ifi.hase.soprafs23.WebsocketDTO;

import ch.uzh.ifi.hase.soprafs23.websocketDto.GameUsersDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class GameUsersDTOTest {
    @Test
    void getType_shouldReturnCorrectType() {
        Assertions.assertEquals("gameUsers", GameUsersDTO.TYPE);
    }

    @Test
    void getHostUsername_shouldReturnCorrectHostUsername() {
        GameUsersDTO gameUsersDTO = new GameUsersDTO();
        gameUsersDTO.setHostUsername("host123");

        Assertions.assertEquals("host123", gameUsersDTO.getHostUsername());
    }

    @Test
    void getUsernames_shouldReturnCorrectUsernames() {
        GameUsersDTO gameUsersDTO = new GameUsersDTO();
        List<String> usernames = Arrays.asList("user1", "user2", "user3");
        gameUsersDTO.setUsernames(usernames);

        Assertions.assertEquals(usernames, gameUsersDTO.getUsernames());
    }
}
