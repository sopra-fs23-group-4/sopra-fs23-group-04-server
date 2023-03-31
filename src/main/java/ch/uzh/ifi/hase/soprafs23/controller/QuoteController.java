package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class QuoteController {
  private final QuoteService quoteService;
  QuoteController(QuoteService quoteService){
    this.quoteService= quoteService;
  }

  @GetMapping(value = "/quotes/{category}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public JsonNode getQuote(@PathVariable("category") String category) throws Exception {
    QuoteCategory quoteCategory=QuoteCategory.getQuoteByType(category);

    String apiKey="rpmvnuWnHglloTTHc7O7ug==8RuxI4PTjhoVUFng";

    URL url = new URL("https://api.api-ninjas.com/v1/chucknorris");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("accept", "application/json");
    connection.setRequestProperty("X-Api-Key", apiKey);
    InputStream responseStream = connection.getInputStream();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(responseStream);
    System.out.println(root.toString());

    return root;


  }
}
