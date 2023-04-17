package ch.uzh.ifi.hase.soprafs23.entity.game;

import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.UserRole;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.uzh.ifi.hase.soprafs23.constant.UserRole.HOST;
import static ch.uzh.ifi.hase.soprafs23.constant.UserRole.PLAYER;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @Column(nullable = false, unique = true)
    private int gamePin;

    @Column(nullable = false)
    private RoundLength roundLength;

    @Column(nullable = false)
    private Long rounds;

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
    @MapKeyColumn(name = "role")
    private Map<UserRole, User> users = new HashMap<>();

    public Long getGameId() {
        return gameId;
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
        return rounds;
    }

    public void setRounds(Long rounds) {
        this.rounds = rounds;
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

    public Map<UserRole, User> getUsers() {
        return users;
    }

    public void addHost(User user) {
        this.users.put(HOST, user);
    }

    public void addPlayer(User user) {
        this.users.put(PLAYER, user);
    }

}