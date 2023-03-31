package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;

@Service
@Transactional
public class QuoteService {
  String apiKey="rpmvnuWnHglloTTHc7O7ug==8RuxI4PTjhoVUFng";
  private final Logger log = LoggerFactory.getLogger(QuoteService.class);

  public QuoteHolder generateQuote(QuoteCategory quoteCategory) throws IOException {

    URL url = new URL(quoteCategory.url);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("accept", "application/json");
    connection.setRequestProperty("X-Api-Key", apiKey);
    try {

      InputStream responseStream = connection.getInputStream();

      ObjectMapper mapper = new ObjectMapper();

      JsonNode jsonResponse = mapper.readTree(responseStream);

      System.out.println(jsonResponse.toString());

      //verifyNotError(jsonResponse, quoteCategory);

      QuoteHolder quoteHolder = new QuoteHolder();
      quoteHolder.setQuote(jsonResponse.get(0).get(quoteCategory.fieldName).asText());

      quoteHolder.setType(quoteCategory.fieldName);

      return quoteHolder;}

    catch (IOException ioException){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The api key is wrong");
    }






  }

  private void verifyNotError(JsonNode jsonResponse, QuoteCategory quoteCategory) {
    Iterator<String> fieldNames = jsonResponse.fieldNames();
    boolean isValid=false;

    while(fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      if (Objects.equals(fieldName, quoteCategory.fieldName)){
        isValid=true;
      }
    }
    if (!isValid){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A "+ fieldNames.toString()+ " The text: ");
    }
  }

}
