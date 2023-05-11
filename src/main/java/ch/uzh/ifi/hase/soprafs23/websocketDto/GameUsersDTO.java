package ch.uzh.ifi.hase.soprafs23.websocketDto;

import java.util.List;

public class GameUsersDTO {
    public static final String TYPE = "gameUsers";

    public String getType(){
        return TYPE;
    }
    private String hostUsername;
    private List<String> usernames;

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
}
