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
    String apiKey = "rpmvnuWnHglloTTHc7O7ug==8RuxI4PTjhoVUFngff";

    private final Logger log = LoggerFactory.getLogger(QuoteService.class);

    private final QuoteApiCaller quoteApiCaller;

    public QuoteService(QuoteApiCaller quoteApiCaller) {
        this.quoteApiCaller = quoteApiCaller;
    }

    public QuoteHolder generateQuote(QuoteCategory quoteCategory) {
        String jsonResponseString = quoteApiCaller.callApi(quoteCategory, apiKey);
        if (jsonResponseString == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quote not found for category: " + quoteCategory.categoryName);
        }
        System.out.println("jsonResponseString: " + jsonResponseString);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = null;
        try {
            jsonResponse = mapper.readTree(jsonResponseString);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON response", e);
        }
        System.out.println("jsonResponse: " + jsonResponse);

        return quoteCategory.extractJsonData.jsonToQuoteHolder(jsonResponse, quoteCategory);
    }


    private void verifyNotError(JsonNode jsonResponse, QuoteCategory quoteCategory) {

        Iterator<String> fieldNames = jsonResponse.fieldNames();
        boolean isValid = false;

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (Objects.equals(fieldName, quoteCategory.fieldName)){
                isValid = true;
            }
        }
        if (!isValid){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A " + fieldNames + " The text: ");
        }
    }

    public QuoteCategoriesHolder getCategories(){
        List<String> categories = QuoteCategory.getAllCategoryNames();

        QuoteCategoriesHolder quoteCategoriesHolder = new QuoteCategoriesHolder();
        quoteCategoriesHolder.setCategories(categories);

        return  quoteCategoriesHolder;
    }

}
