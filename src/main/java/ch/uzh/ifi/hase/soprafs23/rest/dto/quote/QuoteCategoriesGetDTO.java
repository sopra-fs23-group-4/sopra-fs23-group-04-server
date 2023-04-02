package ch.uzh.ifi.hase.soprafs23.rest.dto.quote;

import java.util.List;

public class QuoteCategoriesGetDTO {
    private List<String> categories;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
