package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
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
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class QuoteService {
    private final String apiKey="Jcn3zBSrMNcsCeGm5rSm5zUabLibdT4xZiliT2rX";
    private final Logger log = LoggerFactory.getLogger(QuoteService.class);

    public QuoteHolder generateQuote(String category)  {

        QuoteCategory quoteCategory=QuoteCategory.getQuoteByCategory(category);

        try {
            URL url = new URL(quoteCategory.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("X-Api-Key", apiKey);

            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseStream);
            System.out.println(jsonResponse.toString());

            return quoteCategory.extractJsonData.jsonToQuoteHolder(jsonResponse,quoteCategory);


        } catch (MalformedURLException e) {
            System.err.println("Error: Invalid URL for quote category: " + quoteCategory.categoryName);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Problem connecting to the API for quote category: " + quoteCategory.categoryName+ "possible reasons could be wrong api or no internet access");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"The server has an issue with the api key or not able to connect to api");
        }
        catch (Error e){
            System.err.println("Something went wrong " + quoteCategory.categoryName);
            e.printStackTrace();
        }
        return null;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A "+ fieldNames+ " The text: ");
        }
    }
    public QuoteCategoriesHolder getCategories(){
        List<String> categories=QuoteCategory.getAllCategoryNames();

        QuoteCategoriesHolder quoteCategoriesHolder= new QuoteCategoriesHolder();
        quoteCategoriesHolder.setCategories(categories);

        return  quoteCategoriesHolder;
    }

}
