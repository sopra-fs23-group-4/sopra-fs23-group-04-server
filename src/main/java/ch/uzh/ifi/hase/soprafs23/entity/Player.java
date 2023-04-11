package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="PLAYER")
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Long id;


    private int lobbyId;
    private int totalPoints;
    private int roundPoints;
    @Column(nullable = false)
    private String token;

    public int getRoundPoints() {
        return roundPoints;
    }

    public void setRoundPoints(int roundPoints) {
        this.roundPoints = roundPoints;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int gameId) {
        this.lobbyId = gameId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}
