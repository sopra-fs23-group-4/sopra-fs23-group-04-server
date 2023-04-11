package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.List;


public class Game {
    private Long id;
    private String pin;
    private List<Player> players;

    private boolean hasStarted;
    private String hostToken;
    private int numberOfRounds;
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String hostId) {
        this.hostToken = hostId;
    }




    public Game() {

    }

    public Long getId() {
        return id;
    }

    public boolean inGame(String token){
        for (Player player : players){
            if (player.getToken()==token){
                return true;
            }
        }
        return false;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }
}

