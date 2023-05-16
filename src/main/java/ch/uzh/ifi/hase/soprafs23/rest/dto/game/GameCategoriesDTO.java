package ch.uzh.ifi.hase.soprafs23.rest.dto.game;

import java.util.List;

public class GameCategoriesDTO {
    private List<String> categories;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
