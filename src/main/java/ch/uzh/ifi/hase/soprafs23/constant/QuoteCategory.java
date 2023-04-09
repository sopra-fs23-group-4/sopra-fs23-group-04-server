package ch.uzh.ifi.hase.soprafs23.constant;

import ch.uzh.ifi.hase.soprafs23.constant.extractJsonData.ExtractJsonArrayConvertor;
import ch.uzh.ifi.hase.soprafs23.constant.extractJsonData.ExtractJsonData;
import ch.uzh.ifi.hase.soprafs23.constant.extractJsonData.ExtractJsonElementConvertor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum QuoteCategory {


    DADJOKE("https://api.api-ninjas.com/v1/dadjokes?limit=1", "dadJoke", "joke"),
    CHUCKNORRIS("https://api.api-ninjas.com/v1/chucknorris?","chucknorris","joke", new ExtractJsonElementConvertor()),
    JOKE("https://api.api-ninjas.com/v1/jokes?limit=1","joke","joke"),


    BUSINESS(baseUrlQuote("business"), "business", "quote"),
    CAR(baseUrlQuote("car"), "car", "quote"),
    COMPUTERS(baseUrlQuote("computers"), "computers", "quote"),
    COOL(baseUrlQuote("cool"), "cool", "quote"),
    COURAGE(baseUrlQuote("courage"), "courage", "quote"),
    DAD(baseUrlQuote("dad"), "dad", "quote"),
    DATING(baseUrlQuote("dating"), "dating", "quote"),
    DEATH(baseUrlQuote("death"), "death", "quote"),
    FRIENDSHIP(baseUrlQuote("friendship"), "friendship", "quote"),
    FUNNY(baseUrlQuote("funny"), "funny", "quote"),
    FUTURE(baseUrlQuote("future"), "future", "quote"),
    GREAT(baseUrlQuote("great"), "great", "quote"),
    HAPPINESS(baseUrlQuote("happiness"), "happiness", "quote"),
    HUMOR(baseUrlQuote("humor"), "humor", "quote"),
    IMAGINATION(baseUrlQuote("imagination"), "imagination", "quote"),
    INSPIRATIONAL(baseUrlQuote("inspirational"), "inspirational", "quote"),
    JEALOUSY(baseUrlQuote("jealousy"), "jealousy", "quote"),
    LIFE(baseUrlQuote("life"), "life", "quote"),
    LOVE(baseUrlQuote("love"), "love", "quote"),
    MONEY(baseUrlQuote("money"), "money", "quote"),
    MOVIES(baseUrlQuote("movies"), "movies", "quote"),
    SUCCESS(baseUrlQuote("success"), "success", "quote");


    public final String url;
    public final String categoryName;
    public final String fieldName;
    public final ExtractJsonData extractJsonData;

    QuoteCategory(String url, String categoryName, String fieldName) {
        this.url = url;
        this.categoryName = categoryName;
        this.fieldName = fieldName;
        this.extractJsonData=new ExtractJsonArrayConvertor();
    }

    QuoteCategory(String url, String categoryName, String fieldName, ExtractJsonData extractJsonData){
        this.url = url;
        this.categoryName = categoryName;
        this.fieldName = fieldName;
        this.extractJsonData = extractJsonData;
    }
    private static String baseUrlQuote(String category) {
        return "https://api.api-ninjas.com/v1/quotes?category=" + category;
    }

    public static QuoteCategory getQuoteByCategory(String categoryName) {
        for (QuoteCategory quoteCategory : QuoteCategory.values()) {
          if (quoteCategory.categoryName.equalsIgnoreCase(categoryName)) {
            return quoteCategory;
          }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The category" + categoryName + " doens't exist");
  }

  public static List<String> getAllCategoryNames(){
        List<String> categories = new ArrayList<>();

        for (QuoteCategory quoteCategory: QuoteCategory.values()){
            categories.add(quoteCategory.categoryName);
        }
        Collections.sort(categories);

        return categories;
  }



}
