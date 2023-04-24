package ch.uzh.ifi.hase.soprafs23.entity.game;

import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @Column(nullable = false, unique = true)
    private Long hostId;

    @Column(nullable = false, unique = true)
    private int gamePin;

    @Column(nullable = false)
    private RoundLength roundLength;

    @Column(nullable = false)
    private Long roundAmount;

    @Column(nullable = false)
    private GameStatus status;

    @ElementCollection
    @OrderColumn
    private List<Character> roundLetters;

    /** create intermediary table between game and category */
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "GAME_CATEGORY",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "GAME_USER",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds = new ArrayList<>();

    public Long getGameId() {
        return gameId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public int getGamePin() {
        return gamePin;
    }

    public void setGamePin(int gamePin) {
        this.gamePin = gamePin;
    }

    public RoundLength getRoundLength() {
        return roundLength;
    }

    public void setRoundLength(RoundLength roundLength) {
        this.roundLength = roundLength;
    }

    public Long getRounds() {
        return roundAmount;
    }

    public void setRounds(Long roundAmount) {
        this.roundAmount = roundAmount;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<Character> getRoundLetters() {
        return roundLetters;
    }

    public void setRoundLetters(List<Character> roundLetters) {
        this.roundLetters = roundLetters;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addPlayer(User user) {
        this.users.add(user);
    }

    public void removePlayer(User user) {
        this.users.remove(user);
    }

    public void addRound(Round round) {
        rounds.add(round);
    }

    public void removeRound(Round round) {
        rounds.remove(round);
    }

}
