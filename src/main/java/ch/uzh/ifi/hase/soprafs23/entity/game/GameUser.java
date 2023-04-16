package ch.uzh.ifi.hase.soprafs23.entity.game;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "GAME_USER")
public class GameUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameUserId;

    @Column(unique = true)
    private Long gameId;

    @Column(nullable = false)
    private Long userId;
}
