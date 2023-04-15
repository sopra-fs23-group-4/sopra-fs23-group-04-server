package ch.uzh.ifi.hase.soprafs23.entity.game;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class GameUser {
    @Id
    @GeneratedValue
    private Long gameUserId;

    @Column(nullable = false, unique = true)
    private Long gameId;

    @Column(nullable = false, unique = true)
    private Long userId;
}
