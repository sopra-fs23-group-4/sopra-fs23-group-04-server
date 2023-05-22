package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class PlayerLeftDTO {
    public static final String type="playerLeft";

    public String getType(){
        return type;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
