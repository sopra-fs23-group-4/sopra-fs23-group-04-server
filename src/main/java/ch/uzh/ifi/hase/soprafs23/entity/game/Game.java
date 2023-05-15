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
    private static final int serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private int gameId;

    @Column(nullable = false)
    private int hostId;

    @Column(nullable = false, unique = true)
    private int gamePin;

    @Column(nullable = false)
    private RoundLength roundLength;

    @Column(nullable = false)
    private int roundAmount;

    @Column(nullable = false)
    private GameStatus status;

    @Column(nullable = false)
    private int numberOfCategories;

    private int currentRound;

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

    public int getNumberOfCategories() {
        return numberOfCategories;
    }

    public void setNumberOfCategories(int numberOfCategories) {
        this.numberOfCategories = numberOfCategories;
    }

    public int getGameId() {
        return gameId;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public int getGamePin() { return gamePin; }

    public void setGamePin(int gamePin) {
        this.gamePin = gamePin;
    }

    public RoundLength getRoundLength() {
        return roundLength;
    }

    public void setRoundLength(RoundLength roundLength) {
        this.roundLength = roundLength;
    }

    public int getRounds() {
        return roundAmount;
    }

    public void setRounds(int roundAmount) {
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

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
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
    public int incrementRound(){
        this.currentRound+=1;
        return currentRound;
    }



}
