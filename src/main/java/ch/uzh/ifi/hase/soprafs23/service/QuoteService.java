package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.Quote;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

@Service
@Transactional
public class QuoteService {
  String apiKey="rpmvnuWnHglloTTHc7O7ug==8RuxI4PTjhoVUFng";
  private final Logger log = LoggerFactory.getLogger(QuoteService.class);

  public Quote getQuote(QuoteCategory quoteCategory) throws IOException {

    URL url = new URL(quoteCategory.url);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("accept", "application/json");
    connection.setRequestProperty("X-Api-Key", apiKey);

    InputStream responseStream = connection.getInputStream();
    ObjectMapper mapper = new ObjectMapper();


    JsonNode root = mapper.readTree(responseStream);
    verifyNotError(root);


    System.out.println(root.path("fact").asText());
    return null;

  }

  private void verifyNotError(JsonNode root) {
    Iterator<String> fieldNames = root.fieldNames();

    while(fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      if (fieldName.toLowerCase().strip()=="error" || fieldName.toLowerCase().strip()== "message"){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A "+ fieldName+ " The text: "+ root.get(fieldName).asText());
      }
    }
  }
}
