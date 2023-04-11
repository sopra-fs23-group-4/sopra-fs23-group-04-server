package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerDTO {
    private int lobbyId;
    private String token;
    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
