package ch.uzh.ifi.hase.soprafs23.entity.game;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Category {

    @Id
    @GeneratedValue
    private Long categoryId;

    @Column(nullable = false, unique = true)
    private String categoryName;

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
