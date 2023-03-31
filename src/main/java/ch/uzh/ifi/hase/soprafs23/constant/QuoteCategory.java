package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum QuoteCategory {

    DADJOKE("https://api.api-ninjas.com/v1/dadjokes?limit=1", "dadJoke");


    public final String url;
    public final String categoryname;

    QuoteCategory(String url, String categoryname) {
        this.url = url;
        this.categoryname = categoryname;
    }
  public static QuoteCategory getQuoteByType(String categoryname) {
    for (QuoteCategory quoteCategory : QuoteCategory.values()) {
      if (quoteCategory.categoryname.equalsIgnoreCase(categoryname)) {
        return quoteCategory;
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The category" + categoryname + " doens't exist");
  }



}
