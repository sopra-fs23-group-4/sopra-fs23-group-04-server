package ch.uzh.ifi.hase.soprafs23.entity.game;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class GameCategory {
    @Id
    @GeneratedValue
    private Long gameCategoryId;

    @Column(nullable = false)
    private Long gameId;

    @Column(nullable = false)
    private Long categoryId;

    public Long getGameCategoryId() {
        return gameCategoryId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
