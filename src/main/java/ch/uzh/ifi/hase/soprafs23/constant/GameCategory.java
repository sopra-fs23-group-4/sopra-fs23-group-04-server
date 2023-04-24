package ch.uzh.ifi.hase.soprafs23.constant;

import java.util.ArrayList;
import java.util.List;

public enum GameCategory {
    CITY("City"),
    COUNTRY("Country"),
    FRUITS("Fruits"),
    ANIMALS("Animals"),
    COUNTRIES("Countries"),
    COLORS("Colors"),
    VEGETABLES("Vegetables"),
    MOVIES("Movies"),
    SPORTS("Sports"),
    MUSICAL_INSTRUMENTS("Musical Instruments");
    private final String name;

    GameCategory(String name) {
        this.name = name;
    }

    public  String getName() {
        return name;
    }

    public static List<String> getCategories(){
        List <String> categories=new ArrayList<>();
        for (GameCategory gameCategory:GameCategory.values()){
            categories.add(gameCategory.name);
        }
        return categories;
    }


}
