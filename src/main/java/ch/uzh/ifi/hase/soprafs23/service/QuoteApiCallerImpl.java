package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class QuoteApiCallerImpl implements QuoteApiCaller {
    @Override
    public String callApi(QuoteCategory quoteCategory, String apiKey) {
        try {
            URL url = new URL(quoteCategory.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("X-Api-Key", apiKey);

            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseStream);
            return jsonResponse.toString();

        } catch (MalformedURLException e) {
            System.err.println("Error: Invalid URL for quote category: " + quoteCategory.categoryName);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Problem connecting to the API for quote category: " + quoteCategory.categoryName);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The server has an issue with the api key");
        } catch (Error e) {
            System.err.println("Something went wrong " + quoteCategory.categoryName);
            e.printStackTrace();
        }
        return null;
    }
}
