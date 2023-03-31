package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum QuoteCategory {


    DADJOKE("https://api.api-ninjas.com/v1/dadjokes?limit=1", "dadJoke", "joke"),

    INSPIRATIONALQUOTE("https://api.api-ninjas.com/v1/quotes?category=inspirational","inspirational","quote");


    public final String url;
    public final String categoryName;
    public final String fieldName;

    QuoteCategory(String url, String categoryName, String fieldName) {
        this.url = url;
        this.categoryName = categoryName;
        this.fieldName = fieldName;
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
