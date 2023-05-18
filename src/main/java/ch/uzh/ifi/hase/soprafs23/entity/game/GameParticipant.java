package ch.uzh.ifi.hase.soprafs23.entity.game;

import ch.uzh.ifi.hase.soprafs23.constant.ParticipantStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "GAME_PARTICIPANT")
public class GameParticipant {

    public GameParticipant(Game game, User user, ParticipantStatus status) {
        this.game = game;
        this.user = user;
        this.participantStatus = status;
    }

    public GameParticipant() {
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private ParticipantStatus participantStatus;  // INGAME, LEFT


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParticipantStatus getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(ParticipantStatus participantStatus) {
        this.participantStatus = participantStatus;
    }
}