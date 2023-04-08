package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;

@Entity
@Table(name = "GAME_USER_MAP")
public class GameUserMap {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //getters and setters
}