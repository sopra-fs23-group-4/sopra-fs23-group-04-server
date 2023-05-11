package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.extract_api_call_data.FactJsonExtractor;
import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
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
import java.util.List;

import static ch.uzh.ifi.hase.soprafs23.constant.Constant.API_KEY;

@Service
@Transactional
public class QuoteService {

    private final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    public QuoteHolder generateQuote(String category)  {

        QuoteCategory quoteCategory=QuoteCategory.getQuoteByCategory(category);

        try {
            URL url = new URL(quoteCategory.url);
            JsonNode apiResponse = callApi(url);

            return quoteCategory.extractJsonData.jsonToQuoteHolder(apiResponse,quoteCategory);


        } catch (MalformedURLException e) {
            logger.info("Error: Invalid URL for quote category: " + quoteCategory.categoryName);
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("Error: Problem connecting to the API for quote category: " + quoteCategory.categoryName+ "possible reasons could be wrong api or no internet access");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"The server has an issue with the api key or not able to connect to api");
        }
        catch (Error e){
            logger.info("Something went wrong " + quoteCategory.categoryName);
            e.printStackTrace();
        }
        return null;

    }

    public QuoteCategoriesHolder getCategories(){
        List<String> categories=QuoteCategory.getAllCategoryNames();

        QuoteCategoriesHolder quoteCategoriesHolder= new QuoteCategoriesHolder();
        quoteCategoriesHolder.setCategories(categories);

        return  quoteCategoriesHolder;
    }

    public FactHolder generateFact(){
        try {
            URL url = new URL("https://api.api-ninjas.com/v1/facts?limit=1");
            JsonNode apiResponse = callApi(url);

            FactJsonExtractor factJsonExtractor= jsonResponse -> {
                FactHolder factHolder=new FactHolder();
                factHolder.setFact(jsonResponse.get(0).get("fact").asText());
                return factHolder;
            };
            return factJsonExtractor.jsonToFactHolder(apiResponse);


        } catch (MalformedURLException e) {
            logger.info("Error: Invalid URL for quote fact");
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("Error: Problem connecting to the API for quote category: fact" + "possible reasons could be wrong api or no internet access");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"The server has an issue with the api key or not able to connect to api");
        }
        catch (Error e){
            logger.info("Something went wrong fact" );
            e.printStackTrace();
        }
        return null;
    }

    private JsonNode callApi(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("X-Api-Key", API_KEY);

        InputStream responseStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(responseStream);
        logger.info(jsonResponse.toString());
        return jsonResponse;
    }

}
