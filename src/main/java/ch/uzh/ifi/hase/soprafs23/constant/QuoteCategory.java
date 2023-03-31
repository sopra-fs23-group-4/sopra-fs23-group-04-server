package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum QuoteCategory {


    DADJOKE("https://api.api-ninjas.com/v1/dadjokes?limit=1", "dadJoke", "joke"),
    CHUCKNORRIS("https://api.api-ninjas.com/v1/chucknorris?","chucknorris","joke"), 

    AGE(baseUrl("age"), "age", "quote"),
    ALONE(baseUrl("alone"), "alone", "quote"),
    AMAZING(baseUrl("amazing"), "amazing", "quote"),
    ANGER(baseUrl("anger"), "anger", "quote"),
    ARCHITECTURE(baseUrl("architecture"), "architecture", "quote"),
    ART(baseUrl("art"), "art", "quote"),
    ATTITUDE(baseUrl("attitude"), "attitude", "quote"),
    BEAUTY(baseUrl("beauty"), "beauty", "quote"),
    BEST(baseUrl("best"), "best", "quote"),
    BIRTHDAY(baseUrl("birthday"), "birthday", "quote"),
    BUSINESS(baseUrl("business"), "business", "quote"),
    CAR(baseUrl("car"), "car", "quote"),
    CHANGE(baseUrl("change"), "change", "quote"),
    COMMUNICATIONS(baseUrl("communications"), "communications", "quote"),
    COMPUTERS(baseUrl("computers"), "computers", "quote"),
    COOL(baseUrl("cool"), "cool", "quote"),
    COURAGE(baseUrl("courage"), "courage", "quote"),
    DAD(baseUrl("dad"), "dad", "quote"),
    DATING(baseUrl("dating"), "dating", "quote"),
    DEATH(baseUrl("death"), "death", "quote"),
    DESIGN(baseUrl("design"), "design", "quote"),
    DREAMS(baseUrl("dreams"), "dreams", "quote"),
    EDUCATION(baseUrl("education"), "education", "quote"),
    ENVIRONMENTAL(baseUrl("environmental"), "environmental", "quote"),
    EQUALITY(baseUrl("equality"), "equality", "quote"),
    EXPERIENCE(baseUrl("experience"), "experience", "quote"),
    FAILURE(baseUrl("failure"), "failure", "quote"),
    FAITH(baseUrl("faith"), "faith", "quote"),
    FAMILY(baseUrl("family"), "family", "quote"),
    FAMOUS(baseUrl("famous"), "famous", "quote"),
    FEAR(baseUrl("fear"), "fear", "quote"),
    FITNESS(baseUrl("fitness"), "fitness", "quote"),
    FOOD(baseUrl("food"), "food", "quote"),
    FORGIVENESS(baseUrl("forgiveness"), "forgiveness", "quote"),
    FREEDOM(baseUrl("freedom"), "freedom", "quote"),
    FRIENDSHIP(baseUrl("friendship"), "friendship", "quote"),
    FUNNY(baseUrl("funny"), "funny", "quote"),
    FUTURE(baseUrl("future"), "future", "quote"),
    GOD(baseUrl("god"), "god", "quote"),
    GOOD(baseUrl("good"), "good", "quote"),
    GOVERNMENT(baseUrl("government"), "government", "quote"),
    GRADUATION(baseUrl("graduation"), "graduation", "quote"),
    GREAT(baseUrl("great"), "great", "quote"),
    HAPPINESS(baseUrl("happiness"), "happiness", "quote"),
    HEALTH(baseUrl("health"), "health", "quote"),
    HISTORY(baseUrl("history"), "history", "quote"),
    HOME(baseUrl("home"), "home", "quote"),
    HOPE(baseUrl("hope"), "hope", "quote"),
    HUMOR(baseUrl("humor"), "humor", "quote"),
    IMAGINATION(baseUrl("imagination"), "imagination", "quote"),
    INSPIRATIONAL(baseUrl("inspirational"), "inspirational", "quote"),
    INTELLIGENCE(baseUrl("intelligence"), "intelligence", "quote"),
    JEALOUSY(baseUrl("jealousy"), "jealousy", "quote"),
    KNOWLEDGE(baseUrl("knowledge"), "knowledge", "quote"),
    LEADERSHIP(baseUrl("leadership"), "leadership", "quote"),
    LEARNING(baseUrl("learning"), "learning", "quote"),
    LEGAL(baseUrl("legal"), "legal", "quote"),
    LIFE(baseUrl("life"), "life", "quote"),
    LOVE(baseUrl("love"), "love", "quote"),
    MARRIAGE(baseUrl("marriage"), "marriage", "quote"),
    MEDICAL(baseUrl("medical"), "medical", "quote"),
    MEN(baseUrl("men"), "men", "quote"),
    MOM(baseUrl("mom"), "mom", "quote"),
    MONEY(baseUrl("money"), "money", "quote"),
    MORNING(baseUrl("morning"), "morning", "quote"),
    MOVIES(baseUrl("movies"), "movies", "quote"),
    SUCCESS(baseUrl("success"), "success", "quote");


    public final String url;
    public final String categoryName;
    public final String fieldName;

    QuoteCategory(String url, String categoryName, String fieldName) {
        this.url = url;
        this.categoryName = categoryName;
        this.fieldName = fieldName;
    }
    private static String baseUrl(String category) {
        return "https://api.api-ninjas.com/v1/quotes?category=" + category;
    }

    public static QuoteCategory getQuoteByType(String categoryName) {
    for (QuoteCategory quoteCategory : QuoteCategory.values()) {
      if (quoteCategory.categoryName.equalsIgnoreCase(categoryName)) {
        return quoteCategory;
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The category" + categoryName + " doens't exist");
  }



}
